package com.Projeto.GeradorDeQuestoes.controllers;

import com.Projeto.GeradorDeQuestoes.repositories.VectorStoreRepository; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/topicos")
public class TopicoController {

    private final VectorStoreRepository vectorStoreRepository;

    public TopicoController(VectorStoreRepository vectorStoreRepository) {
        this.vectorStoreRepository = vectorStoreRepository;
    }

    @GetMapping
    public ResponseEntity<List<String>> getTopicosDisponiveis() {
        List<String> topicos = vectorStoreRepository.findDistinctTopicos();
        return ResponseEntity.ok(topicos);
    }

    @GetMapping("/{topico}/conceitos")
    public ResponseEntity<List<String>> getConceitosPorTopico(@PathVariable String topico) {
        List<String> conceitos = vectorStoreRepository.findDistinctConceitosByTopico(topico);
        return ResponseEntity.ok(conceitos);
    }
}
