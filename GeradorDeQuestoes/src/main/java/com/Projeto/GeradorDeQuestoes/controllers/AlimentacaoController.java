package com.Projeto.GeradorDeQuestoes.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.Projeto.GeradorDeQuestoes.services.AlimentacaoService;

@RestController
@RequestMapping("/api/alimentacao")
@CrossOrigin(origins = "http://localhost:4200")
public class AlimentacaoController {
    private final AlimentacaoService alimentacaoService;

    public AlimentacaoController(AlimentacaoService alimentacaoService) {
        this.alimentacaoService = alimentacaoService;
    }

    @PostMapping("/upload-pdf")
    public ResponseEntity<String> uploadPdf(
            @RequestParam("file") MultipartFile file, 
            @RequestParam("topico") String topico
            ) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Arquivo está vazio.");
        }
        if (topico == null || topico.isBlank()) {
            return ResponseEntity.badRequest().body("O tópico não pode ser vazio.");
        }

        try {
            alimentacaoService.processarPdf(file, topico);
            return ResponseEntity.ok("Arquivo processado e salvo no RAG com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Falha ao processar o PDF: " + e.getMessage());
        }
    }
}
