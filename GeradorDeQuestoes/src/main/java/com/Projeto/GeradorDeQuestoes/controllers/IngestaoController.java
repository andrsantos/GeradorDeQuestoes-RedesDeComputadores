package com.Projeto.GeradorDeQuestoes.controllers;

import java.io.File;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Projeto.GeradorDeQuestoes.dto.QuestaoDTO;
import com.Projeto.GeradorDeQuestoes.services.IngestaoMaterialService;

@RestController
@RequestMapping("/api/admin/material")
@CrossOrigin(origins = "*") 
public class IngestaoController {

    private final IngestaoMaterialService ingestaoService;

    public IngestaoController(IngestaoMaterialService ingestaoService) {
        this.ingestaoService = ingestaoService;
    }


    @PostMapping("/upload/dificil")
    public ResponseEntity<String> uploadMaterialDificil(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {
        
            try {
                    byte[] bytes = file.getBytes();
                    Resource pdfResource = new ByteArrayResource(bytes) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename(); 
                        }
                    };

                    ingestaoService.importarCapituloLivroDificil(pdfResource, topico, fonte);
                    
                    return ResponseEntity.ok("Material processado e indexado com sucesso no PGVector!");
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao processar PDF: " + e.getMessage());
                }
    }

    @PostMapping("/upload/medio")
    public ResponseEntity<String> uploadMaterialMedio(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {
        
        try {
            Resource pdfResource = file.getResource();
            
            ingestaoService.importarCapituloLivroMedio(pdfResource, topico, fonte);
            
            return ResponseEntity.ok("Material processado e indexado com sucesso no PGVector!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar PDF: " + e.getMessage());
        }
    }

    @PostMapping("/upload/facil")
    public ResponseEntity<String> uploadMaterialFacil(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {
        
        try {
            Resource pdfResource = file.getResource();
            
            ingestaoService.importarCapituloLivroFacil(pdfResource, topico, fonte);
            
            return ResponseEntity.ok("Material processado e indexado com sucesso no PGVector!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar PDF: " + e.getMessage());
        }
    }

    @PostMapping("/upload/questoes")
    public ResponseEntity<List<QuestaoDTO>> extrairQuestoesDeProva(
            @RequestParam("file") MultipartFile file) {
        
        File tempFile = null;
    try {
        tempFile = File.createTempFile("prova_", ".pdf");
        
        file.transferTo(tempFile);
        
        
        return ResponseEntity.ok(ingestaoService.processarPdfParaQuestoes(tempFile));
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } finally {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }
    }


}