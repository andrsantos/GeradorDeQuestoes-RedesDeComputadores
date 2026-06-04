package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.Projeto.GeradorDeQuestoes.dto.PromptRequestDTO;
import com.Projeto.GeradorDeQuestoes.dto.PromptResponseDTO;
import com.Projeto.GeradorDeQuestoes.entities.PromptEntity;
import com.Projeto.GeradorDeQuestoes.entities.TopicoEntity;
import com.Projeto.GeradorDeQuestoes.repositories.PromptRepository;
import com.Projeto.GeradorDeQuestoes.repositories.TopicoRepository;
import com.Projeto.GeradorDeQuestoes.services.PromptService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PromptServiceImpl implements PromptService {

    private final PromptRepository promptRepository;
    private final TopicoRepository topicoRepository;

    public PromptServiceImpl(PromptRepository promptRepository, TopicoRepository topicoRepository) {
        this.promptRepository = promptRepository;
        this.topicoRepository = topicoRepository;
    }

    public PromptResponseDTO cadastrar(PromptRequestDTO dto) {
            
            TopicoEntity topico = topicoRepository.findByNome(dto.getTopicoNome())
                    .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado: " + dto.getTopicoNome()));

            PromptEntity novoPrompt = new PromptEntity();
            
            novoPrompt.setTopico(topico); 
            novoPrompt.setNivel(dto.getNivel());
            novoPrompt.setInstrucao(dto.getInstrucao());
            novoPrompt.setAtivo(dto.getAtivo());

            PromptEntity promptSalvo = promptRepository.save(novoPrompt);

            return new PromptResponseDTO(
                promptSalvo.getId(),
                topico.getId().toString(), 
                promptSalvo.getNivel(),
                promptSalvo.getInstrucao(),
                promptSalvo.isAtivo()
            );
        }

   public List<PromptResponseDTO> listarPorTopico(String nomeTopico) {
        List<PromptEntity> prompts = promptRepository.findByTopicoNome(nomeTopico);
        
        return prompts.stream()
                .map(p -> new PromptResponseDTO(
                        p.getId(), 
                        p.getTopico().getId().toString(), 
                        p.getNivel(), 
                        p.getInstrucao(), 
                        p.isAtivo()))
                .toList();
    }

    public List<PromptResponseDTO> listarTodos() {
        List<PromptEntity> prompts = promptRepository.findAll();
        
        return prompts.stream()
                .map(p -> new PromptResponseDTO(
                        p.getId(), 
                        p.getTopico().getId().toString(), 
                        p.getNivel(), 
                        p.getInstrucao(), 
                        p.isAtivo()))
                .toList();
    }

    public void deletar(String idString) { 
        if (!promptRepository.existsById(idString)) {
            throw new EntityNotFoundException("Prompt não encontrado para exclusão.");
        }
        promptRepository.deleteById(idString);
    }

    @Override
    public PromptResponseDTO alternarStatus(String idString) {

        PromptEntity prompt = promptRepository.findById(idString)
                .orElseThrow(() -> new EntityNotFoundException("Prompt não encontrado."));

        boolean novoStatus = !prompt.isAtivo();
        prompt.setAtivo(novoStatus);

        if (novoStatus) {
            List<PromptEntity> outrosPrompts = promptRepository.findByTopicoIdAndNivel(
                    prompt.getTopico().getId(), 
                    prompt.getNivel()
            );

            for (PromptEntity outro : outrosPrompts) {
                if (!outro.getId().equals(prompt.getId())) {
                    outro.setAtivo(false);
                }
            }
            promptRepository.saveAll(outrosPrompts); 
        }

        PromptEntity salvo = promptRepository.save(prompt);

        return new PromptResponseDTO(
                salvo.getId(),
                salvo.getTopico().getId().toString(),
                salvo.getNivel(),
                salvo.getInstrucao(),
                salvo.isAtivo()
        );
        
    }

    @Override
    public PromptResponseDTO editar(String idString, PromptRequestDTO dto) {
        PromptEntity prompt = promptRepository.findById(idString)
                .orElseThrow(() -> new EntityNotFoundException("Prompt não encontrado para edição."));

        prompt.setNivel(dto.getNivel());
        prompt.setInstrucao(dto.getInstrucao());
        prompt.setAtivo(dto.getAtivo());

        if (prompt.isAtivo()) {
            List<PromptEntity> outrosPrompts = promptRepository.findByTopicoIdAndNivel(
                    prompt.getTopico().getId(), 
                    prompt.getNivel()
            );

            for (PromptEntity outro : outrosPrompts) {
                if (!outro.getId().equals(prompt.getId())) {
                    outro.setAtivo(false);
                }
            }
            promptRepository.saveAll(outrosPrompts);
        }

        PromptEntity salvo = promptRepository.save(prompt);

        return new PromptResponseDTO(
                salvo.getId(),
                salvo.getTopico().getId().toString(),
                salvo.getNivel(),
                salvo.getInstrucao(),
                salvo.isAtivo()
        );
    }
    
}
