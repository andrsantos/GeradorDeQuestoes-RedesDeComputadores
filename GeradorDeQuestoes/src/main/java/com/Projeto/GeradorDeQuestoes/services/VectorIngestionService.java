package com.Projeto.GeradorDeQuestoes.services;

import java.util.Map;

public interface VectorIngestionService {

    int ingerirPdf(byte[] pdfBytes, String filename, Map<String, Object> metadata);
    
}
