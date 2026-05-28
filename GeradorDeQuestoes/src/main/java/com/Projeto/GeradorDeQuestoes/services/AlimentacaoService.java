package com.Projeto.GeradorDeQuestoes.services;

import org.apache.pdfbox.Loader; 
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AlimentacaoService {

    private final VectorStore vectorStore;

    private static final int CHUNK_SIZE = 2000; 
    private static final int CHUNK_OVERLAP = 100;

    public AlimentacaoService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processarPdf(MultipartFile file, String topico) throws IOException {
        String textoCompleto = extrairTextoDePdf(file.getInputStream());
        
        
        List<String> chunks = quebrarEmChunks(textoCompleto);
        
        System.out.println("Arquivo " + file.getOriginalFilename() + 
                           " processado. Gerou " + chunks.size() + " chunks.");

        List<Document> documentos = new ArrayList<>();
        for (String chunk : chunks) {
            Map<String, Object> metadata = Map.of(
                    "fileName", file.getOriginalFilename(),
                    "topico", topico
            );
            documentos.add(new Document(chunk, metadata));
        }

        vectorStore.add(documentos);
        System.out.println("Chunks salvos no VectorStore!");
    }

    private String extrairTextoDePdf(InputStream inputStream) throws IOException {
        
        try (InputStream is = inputStream) {
            
            byte[] pdfBytes = is.readAllBytes();
            
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }
        }
    }

    private List<String> quebrarEmChunks(String text) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        int start = 0;
        
        while (start < length) {
            int end = Math.min(start + CHUNK_SIZE, length);
            chunks.add(text.substring(start, end));
            start += (CHUNK_SIZE - CHUNK_OVERLAP);
        }
        return chunks;
    }
}