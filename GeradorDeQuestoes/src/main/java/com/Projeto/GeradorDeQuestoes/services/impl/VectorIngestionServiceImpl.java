package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.Projeto.GeradorDeQuestoes.services.VectorIngestionService;

@Service
public class VectorIngestionServiceImpl implements VectorIngestionService {

    private final VectorStore vectorStore;

    

    public VectorIngestionServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public int ingerirPdf(byte[] pdfBytes, String filename, Map<String, Object> metadata) {

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

        TokenTextSplitter splitter = new TokenTextSplitter(512, 128, 5, 5000, true);
        List<Document> chunks = splitter.apply(paginasValidas);

        chunks.forEach(chunk -> chunk.getMetadata().putAll(metadata));

        vectorStore.accept(chunks);
        System.out.println("Chunks inseridos: " + chunks.size());

        return chunks.size();
       
    }
    
}
