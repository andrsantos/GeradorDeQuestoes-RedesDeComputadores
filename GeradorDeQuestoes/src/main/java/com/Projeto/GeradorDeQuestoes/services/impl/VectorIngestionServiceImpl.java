package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.Projeto.GeradorDeQuestoes.dto.ResultadoIngestaoDTO;
import com.Projeto.GeradorDeQuestoes.services.GeradorQuestaoService;
import com.Projeto.GeradorDeQuestoes.services.VectorIngestionService;

@Service
public class VectorIngestionServiceImpl implements VectorIngestionService {

    private final VectorStore vectorStore;

    private final GeradorQuestaoService geradorQuestaoService;


    public VectorIngestionServiceImpl(VectorStore vectorStore, GeradorQuestaoService geradorQuestaoService) {
        this.vectorStore = vectorStore;
        this.geradorQuestaoService = geradorQuestaoService;
    }

    @Override
    public ResultadoIngestaoDTO ingerirPdf(byte[] pdfBytes, String filename, Map<String, Object> metadata) {

        ByteArrayResource resource = new ByteArrayResource(pdfBytes) {
            @Override
            public String getFilename() { return filename; }
        };

        PagePdfDocumentReader reader = new PagePdfDocumentReader(
            resource,
            PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageBottomMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                .withPagesPerDocument(1)
                .build()
        );

        List<Document> paginas = reader.get();
        System.out.println("Páginas lidas: " + paginas.size());

        List<Document> paginasValidas = paginas.stream()
            .filter(d -> d.getText() != null && !d.getText().isBlank())
            .toList();
        System.out.println("Páginas com texto: " + paginasValidas.size());

        if (paginasValidas.isEmpty()) {
            throw new IllegalArgumentException(
                "Nenhum texto extraído do PDF. Verifique se o arquivo não é escaneado."
            );
        }

        // 1. EXTRAÇÃO DO TEXTO COMPLETO
        // Concatenamos todas as páginas válidas do capítulo em uma única String para o Agente Extrator
        String textoCompleto = paginasValidas.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n"));

        // 2. AGENTE EXTRATOR DE CONCEITOS
        // Chamas o seu método existente passando o texto inteiro do capítulo de uma vez
        List<String> conceitosGlobais = geradorQuestaoService.extrairConceitosUnicos(textoCompleto, 20);
        System.out.println("Conceitos globais extraídos do capítulo: " + conceitosGlobais);

        // 3. FATIAMENTO (CHUNK INGESTION)
        // O seu TokenTextSplitter entra em ação para cortar em blocos de 512 tokens
        TokenTextSplitter splitter = new TokenTextSplitter(512, 128, 5, 5000, true);
        List<Document> chunks = splitter.apply(paginasValidas);

        // 4. MAPEAMENTO INTELIGENTE POR CHUNK (Keyword Matching)
        chunks.forEach(chunk -> {
            String chunkTextLower = chunk.getText().toLowerCase();
            
            // Filtramos quais dos 20 conceitos globais aparecem explicitamente neste pedaço de texto
            List<String> conceitosNesteChunk = conceitosGlobais.stream()
                .filter(conceito -> chunkTextLower.contains(conceito.toLowerCase()))
                .toList();

            // Injeta os metadados padrões que vieram do Controller
            chunk.getMetadata().putAll(metadata);
            
            // Amarra o array de conceitos específicos detectados diretamente no metadata deste chunk
            // O pgvector vai serializar isso nativamente como um campo JSONB no Postgres
            chunk.getMetadata().put("conceitos", conceitosNesteChunk);
        });

        vectorStore.accept(chunks);
        System.out.println("Chunks inseridos com sucesso no pgvector: " + chunks.size());

        return new ResultadoIngestaoDTO(chunks.size(), conceitosGlobais);
    }
    
}
