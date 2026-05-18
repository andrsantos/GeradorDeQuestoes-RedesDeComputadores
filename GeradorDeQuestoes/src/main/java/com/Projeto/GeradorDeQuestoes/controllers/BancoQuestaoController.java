package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.dto.QuestaoComOrigemDTO;
import com.Projeto.GeradorDeQuestoes.dto.QuestaoDTO;
import com.Projeto.GeradorDeQuestoes.entities.BancoQuestaoEntity;
import com.Projeto.GeradorDeQuestoes.entities.PdfQuestaoEntity;
import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;
import com.Projeto.GeradorDeQuestoes.enums.TipoQuestao;
import com.Projeto.GeradorDeQuestoes.repositories.BancoQuestaoRepository;
import com.Projeto.GeradorDeQuestoes.repositories.PdfQuestaoRepository;

import org.springframework.web.bind.annotation.RequestBody;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/banco-questoes")
@CrossOrigin(origins = "http://localhost:4200")
public class BancoQuestaoController {

    private final BancoQuestaoRepository repository;

    private final PdfQuestaoRepository pdfQuestaoRepository;

    public BancoQuestaoController(BancoQuestaoRepository repository, PdfQuestaoRepository pdfQuestaoRepository) {
        this.repository = repository;
        this.pdfQuestaoRepository = pdfQuestaoRepository;
    }

    @PostMapping
    public ResponseEntity<BancoQuestaoEntity> criarQuestao(@RequestBody QuestaoDTO questao) {
        BancoQuestaoEntity novaQuestao = new BancoQuestaoEntity();
        novaQuestao.setEnunciado(questao.getEnunciado());
        novaQuestao.setAlternativas(questao.getAlternativas());
        novaQuestao.setRespostaCorreta(questao.getRespostaCorreta());
        novaQuestao.setConceito(questao.getConceito());
        novaQuestao.setCompetencia(questao.getCompetencia());
        novaQuestao.setComentarioTecnico(questao.getComentarioTecnico());
        novaQuestao.setTipo(TipoQuestao.MULTIPLA_ESCOLHA_5);
        novaQuestao.setTopico(questao.getTopico());
        BancoQuestaoEntity salva = repository.save(novaQuestao);
        return ResponseEntity.ok(salva);
    }

    @PostMapping("/cadastrar/pdf/upload")
    public ResponseEntity<PdfQuestaoEntity> cadastrarPdfUpload(
        @RequestParam("file") MultipartFile file) throws IOException {

        PdfQuestaoEntity entidade = new PdfQuestaoEntity();
        entidade.setNomeOriginal(file.getOriginalFilename());
        entidade.setNomeArmazenamento(UUID.randomUUID() + "_" + file.getOriginalFilename());
        entidade.setConteudo(file.getBytes()); 
        entidade.setContentType(file.getContentType());
        entidade.setTamanhoBytes(file.getSize());

        return ResponseEntity.ok(pdfQuestaoRepository.save(entidade));
    }

    @GetMapping("/cadastrar/pdf/{id}/download")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        PdfQuestaoEntity pdf = pdfQuestaoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + pdf.getNomeOriginal() + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf.getConteudo());
    }
    
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarQuestao(@RequestBody QuestaoComOrigemDTO dto) {
    System.out.println("=== DTO RECEBIDO ===");
    System.out.println("tipo: [" + dto.getTipo() + "]");
    System.out.println("nivel: [" + dto.getNivel() + "]");
    System.out.println("enunciado: [" + dto.getEnunciado() + "]");
    System.out.println("origem: [" + dto.getOrigem() + "]");
    System.out.println("===================");
    try {
        BancoQuestaoEntity questao = new BancoQuestaoEntity();
        questao.setTopico(dto.getTopico());
        questao.setEnunciado(dto.getEnunciado());
        questao.setAlternativas(dto.getAlternativas());
        questao.setRespostaCorreta(dto.getRespostaCorreta());
        questao.setConceito(dto.getConceito());
        questao.setComentarioTecnico(dto.getComentarioTecnico());
        questao.setCompetencia(dto.getCompetencia());
        questao.setTipo(dto.getTipo() != null ? TipoQuestao.valueOf(dto.getTipo()) : TipoQuestao.MULTIPLA_ESCOLHA_5);
        questao.setNivel(dto.getNivel() != null ? NivelTecnico.valueOf(dto.getNivel()) : null);
        if (dto.getDataCriacao() != null) questao.setDataCriacao(LocalDateTime.parse(dto.getDataCriacao()));
        if (dto.getOrigem() != null) pdfQuestaoRepository.findById(UUID.fromString(dto.getOrigem())).ifPresent(questao::setArquivoOrigem);

        return ResponseEntity.ok(repository.save(questao));

    } catch (Exception e) {
        System.out.println("ERRO AO SALVAR: " + e.getMessage());
        return ResponseEntity.status(500).body("Erro: " + e.getMessage() + " | DTO tipo=" + dto.getTipo() + " nivel=" + dto.getNivel());
    }
    }

    @GetMapping
    public ResponseEntity<List<BancoQuestaoEntity>> listarTodas() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BancoQuestaoEntity> buscarPorId(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BancoQuestaoEntity> atualizarQuestao(@PathVariable UUID id, @RequestBody BancoQuestaoEntity questaoAtualizada) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questaoAtualizada.setId(id);
        BancoQuestaoEntity salva = repository.save(questaoAtualizada);
        return ResponseEntity.ok(salva);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirQuestao(@PathVariable UUID id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}