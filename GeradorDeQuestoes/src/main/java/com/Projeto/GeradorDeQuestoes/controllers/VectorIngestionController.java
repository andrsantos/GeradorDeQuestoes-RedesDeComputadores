package com.Projeto.GeradorDeQuestoes.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Projeto.GeradorDeQuestoes.dto.ResultadoIngestaoDTO;
import com.Projeto.GeradorDeQuestoes.entities.DocumentosReferenciaEntity;
import com.Projeto.GeradorDeQuestoes.entities.PdfBinarioEntity;
import com.Projeto.GeradorDeQuestoes.services.DocumentosReferenciaService;
import com.Projeto.GeradorDeQuestoes.services.PdfBinarioService;
import com.Projeto.GeradorDeQuestoes.services.VectorIngestionService;

@RestController
@RequestMapping("/api/documentacao")
public class VectorIngestionController {

    private final VectorIngestionService ingestionService;
    private final DocumentosReferenciaService documentosReferenciaService;
    private final PdfBinarioService pdfBinarioService; 

    public VectorIngestionController(
            VectorIngestionService ingestionService, 
            DocumentosReferenciaService documentosReferenciaService,
            PdfBinarioService pdfBinarioService) {
        this.ingestionService = ingestionService;
        this.documentosReferenciaService = documentosReferenciaService;
        this.pdfBinarioService = pdfBinarioService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("topico") String topico,
            @RequestParam("fonte") String fonte) {

        if (file.isEmpty()) {

            return ResponseEntity.badRequest().body(Map.of("erro", "Arquivo vazio"));

        }

        String filename = file.getOriginalFilename();

        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {

            return ResponseEntity.badRequest().body(Map.of("erro", "Apenas arquivos PDF são aceitos"));

        }

        try {

            byte[] bytes = file.getBytes();
            
            PdfBinarioEntity pdfBinario = pdfBinarioService.salvarOuRecuperar(bytes, filename);

            DocumentosReferenciaEntity docReferencia = documentosReferenciaService.vincularContexto(
                pdfBinario, topico, fonte
            );

            Map<String, Object> metadata = new HashMap<>(); 

            metadata.put("topico", topico);
            metadata.put("fonte", fonte);
            // metadata.put("nivel_material", nivel);
            metadata.put("arquivo", filename);
            metadata.put("documento_id", pdfBinario.getId().toString()); 

            ResultadoIngestaoDTO chunks = ingestionService.ingerirPdf(bytes, filename, metadata);


            return ResponseEntity.ok(Map.of(
                "mensagem", "PDF indexado e vinculado com sucesso",
                "id_binario", pdfBinario.getId(),
                "id_referencia", docReferencia.getId(),
                "arquivo", filename,
                "chunks_inseridos", chunks
            ));

        } catch (IllegalArgumentException e) {

             return ResponseEntity.badRequest().body(Map.of("erro", "Parâmetro inválido: " + e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                .body(Map.of("erro", "Falha ao processar PDF: " + e.getMessage()));

        }
    }

    @GetMapping("/download/{idBinario}")
    public ResponseEntity<byte[]> downloadMaterial(@PathVariable UUID idBinario) {
        try {

            PdfBinarioEntity pdf = pdfBinarioService.buscarPorId(idBinario);

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_PDF);
            
            headers.setContentDispositionFormData("attachment", pdf.getNomeOriginal());
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdf.getArquivoBinario());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}