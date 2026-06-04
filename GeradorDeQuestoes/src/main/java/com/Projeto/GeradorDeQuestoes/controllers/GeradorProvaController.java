package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.dto.GeracaoAutomaticaRequest;
import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.Prova;
import com.Projeto.GeradorDeQuestoes.dto.Questao;
import com.Projeto.GeradorDeQuestoes.dto.QuestaoDTO;
import com.Projeto.GeradorDeQuestoes.services.BancoQuestaoService;
import com.Projeto.GeradorDeQuestoes.services.GeradorProvaService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/provas")
@CrossOrigin(origins = "http://localhost:4200")
public class GeradorProvaController {

    private final GeradorProvaService provaService;
    private final BancoQuestaoService bancoQuestaoService;

    public GeradorProvaController(GeradorProvaService provaService, BancoQuestaoService bancoQuestaoService) {
        this.provaService = provaService;
        this.bancoQuestaoService = bancoQuestaoService;
    }


    @PostMapping
    public ResponseEntity<Prova> criarProva() {
        Prova prova = provaService.criarNovaProva();
        return ResponseEntity.ok(prova); 
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<Prova> getProva(@PathVariable UUID id) {
        Prova prova = provaService.getProva(id);
        if (prova == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(prova);
    }

    @PostMapping("/{id}/questoes")
    public ResponseEntity<Prova> adicionarQuestoes(
            @PathVariable UUID id, 
            @RequestBody GerarQuestaoRequest request) {
        
        try {
            Prova prova = provaService.adicionarQuestoes(id, request);
            return ResponseEntity.ok(prova);
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); 
        }
    }

    @PostMapping("/{id}/questoes-automaticas")
    public ResponseEntity<?> adicionarQuestoesAutomatico(
            @PathVariable UUID id, 
            @RequestBody GeracaoAutomaticaRequest request) {
        try {

            request.getTopicos().forEach( topico -> {
                System.out.println("-----Quantidade Dificeis------ " + topico.getQuantidadeDificeis());
                System.out.println("-----Quantidade Médias------ " + topico.getQuantidadeMedias());
                System.out.println("-----Quantidade Fáceis -------" + topico.getQuantidadeFaceis());
            });
            
            Prova prova = provaService.adicionarQuestoesAutomatico(id, request);
            return ResponseEntity.ok(prova);
        } 
        
        catch(EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
        
        catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body(e.getMessage()); 
        }
    }

    @PostMapping("/{id}/prova-banco")
    public ResponseEntity<?> gerarProvaBanco(
            @PathVariable UUID id, 
            @RequestBody GeracaoAutomaticaRequest request) {

        try {
            request.getTopicos().forEach( topico -> {
                System.out.println("-----Quantidade Dificeis------ " + topico.getQuantidadeDificeis());
                System.out.println("-----Quantidade Médias------ " + topico.getQuantidadeMedias());
                System.out.println("-----Quantidade Fáceis -------" + topico.getQuantidadeFaceis());
            });
            
            List<QuestaoDTO> questoesGeradasBanco = bancoQuestaoService.gerarQuestoesParaProva(request);
            
            questoesGeradasBanco.forEach(questao -> {
            System.out.println("Topico: " + questao.getTopico());
            });

            List<Questao> questoesParaConverter = new ArrayList<>();
            questoesGeradasBanco.forEach(questao -> 
                questoesParaConverter.add(new Questao(
                    questao.getId().toString(),
                    questao.getEnunciado(),
                    questao.getAlternativas(),
                    questao.getRespostaCorreta(),
                    questao.getConceito(),
                    questao.getCompetencia(),
                    questao.getComentarioTecnico(),
                    questao.getTopico(),
                    questao.getNivel()
                ))
            );

            questoesParaConverter.forEach(questao -> {
            System.out.println("Topico: " + questao.getTopico());
            });

            Prova prova = provaService.adicionarQuestoesDoBanco(id, questoesParaConverter);
            return ResponseEntity.ok(prova);

        } 
        
        catch(IllegalArgumentException e) {
            e.printStackTrace(); 
            return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
        }
        catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(500).body(e.getMessage()); 
        }
    }

    
    @PostMapping("/{id}/manual")
    public ResponseEntity<Prova> salvarProvaManual(
            @PathVariable UUID id, 
            @RequestBody List<Questao> questoes) { 
        
        try {
            Prova prova = provaService.adicionarQuestoesManuais(id, questoes);
            return ResponseEntity.ok(prova);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}/questoes")
    public ResponseEntity<Prova> descartarQuestao(
            @PathVariable UUID id, 
            @RequestParam int indice) {
        
        try {
            Prova prova = provaService.descartarQuestao(id, indice);
            return ResponseEntity.ok(prova); 
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/finalizar-pdf")
    public ResponseEntity<byte[]> finalizarEBaixarPdf(@PathVariable UUID id) {
        try {
            byte[] pdfBytes = provaService.finalizarEGerarPdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "prova_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}