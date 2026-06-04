package com.Projeto.GeradorDeQuestoes.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Projeto.GeradorDeQuestoes.dto.PromptRequestDTO;
import com.Projeto.GeradorDeQuestoes.dto.PromptResponseDTO;
import com.Projeto.GeradorDeQuestoes.services.PromptService;

@RestController
@RequestMapping("/api/prompts")
public class PromptController {

    private final PromptService promptService;    

    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @PostMapping
    public ResponseEntity<PromptResponseDTO> cadastrarPrompt(@RequestBody @Valid PromptRequestDTO requestDTO) {
        
        PromptResponseDTO responseDTO = promptService.cadastrar(requestDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/topico/{nomeTopico}")
    public ResponseEntity<List<PromptResponseDTO>> listarPromptsPorTopico(@PathVariable String nomeTopico) {
        List<PromptResponseDTO> prompts = promptService.listarPorTopico(nomeTopico);
        return ResponseEntity.ok(prompts);
    }

    @GetMapping
    public ResponseEntity<List<PromptResponseDTO>> listarTodos() {
        List<PromptResponseDTO> prompts = promptService.listarTodos();
        return ResponseEntity.ok(prompts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPrompt(@PathVariable("id") String id) { 
        promptService.deletar(id); 
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PromptResponseDTO> alternarStatus(@PathVariable("id") String id) {
        PromptResponseDTO promptAtualizado = promptService.alternarStatus(id);
        return ResponseEntity.ok(promptAtualizado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromptResponseDTO> editarPrompt(
            @PathVariable("id") String id, 
            @Valid @RequestBody PromptRequestDTO dto) {
        
        PromptResponseDTO promptAtualizado = promptService.editar(id, dto);
        return ResponseEntity.ok(promptAtualizado);
    }




}
