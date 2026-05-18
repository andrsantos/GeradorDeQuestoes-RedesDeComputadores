package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.Projeto.GeradorDeQuestoes.dto.GeracaoAutomaticaRequest;
import com.Projeto.GeradorDeQuestoes.dto.QuestaoDTO;
import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;
import com.Projeto.GeradorDeQuestoes.repositories.BancoQuestaoRepository;
import com.Projeto.GeradorDeQuestoes.services.BancoQuestaoService;


@Service
public class BancoQuestaoServiceImpl implements BancoQuestaoService {

    private BancoQuestaoRepository bancoQuestaoRepository;

    BancoQuestaoServiceImpl(BancoQuestaoRepository bancoQuestaoRepository) {
        this.bancoQuestaoRepository = bancoQuestaoRepository;
    }

    @Override
    public List<QuestaoDTO> listarQuestoes() {
        return bancoQuestaoRepository.findAll().stream()
                .map(entity -> new QuestaoDTO(
                       entity.getId().toString(),
                        entity.getEnunciado(),
                        entity.getAlternativas(),
                        entity.getRespostaCorreta(),
                        entity.getConceito(),
                        entity.getCompetencia(),
                        entity.getComentarioTecnico(),
                        entity.getNivel()
                ))
                .toList();
    }

    @Override
    public List<QuestaoDTO> listarQuestoesPorTopico(String topico) {
        return bancoQuestaoRepository.findByTopico(topico).stream()
                .map(entity -> new QuestaoDTO(
                    entity.getId().toString(),
                        entity.getEnunciado(),
                        entity.getAlternativas(),
                        entity.getRespostaCorreta(),
                        entity.getConceito(),
                        entity.getCompetencia(),
                        entity.getComentarioTecnico(),
                        entity.getNivel()
                ))
                .toList(); 
    }

    @Override
    public List<QuestaoDTO> listarQuestoesPorNivel(String nivel) {
        return bancoQuestaoRepository.findByNivel(nivel).stream()
                     .map(entity -> new QuestaoDTO(
                        entity.getId().toString(),
                        entity.getEnunciado(),
                        entity.getAlternativas(),
                        entity.getRespostaCorreta(),
                        entity.getConceito(),
                        entity.getCompetencia(),
                        entity.getComentarioTecnico(),
                        entity.getNivel()
                ))
                .toList(); 
    }

    @Override
    public List<QuestaoDTO> gerarQuestoesParaProva(GeracaoAutomaticaRequest request) {


        List<QuestaoDTO> questoesGeradas = new ArrayList<>();

       for(int i = 0; i < request.getTopicos().size(); i++){
            String topico = request.getTopicos().get(i).getTopico();
            int quantidadeFaceis = request.getTopicos().get(i).getQuantidadeFaceis();
            int quantidadeMedias = request.getTopicos().get(i).getQuantidadeMedias();
            int quantidadeDificeis = request.getTopicos().get(i).getQuantidadeDificeis();

        List<QuestaoDTO> questoesPorTopico = listarQuestoesPorTopico(topico);

         List<QuestaoDTO> todasFaceis = questoesPorTopico.stream()
         .filter(q -> q.getNivel().equals(NivelTecnico.UNIVERSITARIO_INICIANTE))
         .collect(Collectors.toCollection(ArrayList::new));
         Collections.shuffle(todasFaceis);
         List<QuestaoDTO> questoesFaceis = todasFaceis.stream()
                 .limit(quantidadeFaceis)
                 .toList();

         List<QuestaoDTO> todasMedias = questoesPorTopico.stream()
         .filter(q -> q.getNivel().equals(NivelTecnico.UNIVERSITARIO_INTERMEDIARIO))
         .collect(Collectors.toCollection(ArrayList::new));
         Collections.shuffle(todasMedias);
         List<QuestaoDTO> questoesMedias = todasMedias.stream()
                 .limit(quantidadeMedias)
                 .toList();


         List<QuestaoDTO> todasDificeis = questoesPorTopico.stream()
         .filter(q -> q.getNivel().equals(NivelTecnico.UNIVERSITARIO_AVANCADO))
         .collect(Collectors.toCollection(ArrayList::new));
         Collections.shuffle(todasDificeis);
         List<QuestaoDTO> questoesDificeis = todasDificeis.stream()
                 .limit(quantidadeDificeis)
                 .toList();
        

        List<QuestaoDTO> questoesSelecionadas = Stream.of(questoesFaceis, questoesMedias, questoesDificeis)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

        questoesGeradas.addAll(questoesSelecionadas);

        }
        return questoesGeradas;
    }

    
    
}