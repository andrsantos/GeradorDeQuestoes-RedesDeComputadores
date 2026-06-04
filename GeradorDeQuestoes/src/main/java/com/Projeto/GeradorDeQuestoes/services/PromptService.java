package com.Projeto.GeradorDeQuestoes.services;

import java.util.List;

import com.Projeto.GeradorDeQuestoes.dto.PromptRequestDTO;
import com.Projeto.GeradorDeQuestoes.dto.PromptResponseDTO;

public interface PromptService {
    
    PromptResponseDTO cadastrar(PromptRequestDTO dto);
    List<PromptResponseDTO> listarPorTopico(String nomeTopico);
    List<PromptResponseDTO> listarTodos();
    void deletar(String id);
    PromptResponseDTO alternarStatus(String idString);
    PromptResponseDTO editar(String idString, PromptRequestDTO dto);
}
