package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.Projeto.GeradorDeQuestoes.dto.ConceitoConfigDTO;
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
                        entity.getTopico(),
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

    public List<QuestaoDTO> listaQuestoesPorConceito(String conceito){

            return bancoQuestaoRepository.findByConceito(conceito).stream()
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

        for (int i = 0; i < request.getTopicos().size(); i++) {
            var topicoRequest = request.getTopicos().get(i);
            String topico = topicoRequest.getTopico();

            List<QuestaoDTO> questoesPorTopico = listarQuestoesPorTopico(topico);

            if (questoesPorTopico.isEmpty()) {
                throw new IllegalArgumentException("Não existem questões para o tópico: " + topico + " cadastradas no banco.");
            }

            for (int j = 0; j < topicoRequest.getSubtopicos().size(); j++) {
                ConceitoConfigDTO conceito = topicoRequest.getSubtopicos().get(j);

                int quantidadeFaceis = conceito.getQuantidadeFaceis();
                int quantidadeMedias = conceito.getQuantidadeMedias();
                int quantidadeDificeis = conceito.getQuantidadeDificeis();
                int quantidadeTotalConceito = quantidadeFaceis + quantidadeMedias + quantidadeDificeis;

                if (quantidadeTotalConceito == 0) continue;

                List<QuestaoDTO> questoesPorConceito = questoesPorTopico.stream()
                        .filter(q -> q.getConceito() != null && q.getConceito().equalsIgnoreCase(conceito.getConceito()))
                        .collect(Collectors.toCollection(ArrayList::new));

                if (quantidadeFaceis > 0) {
                    List<QuestaoDTO> faceisDisponiveis = questoesPorConceito.stream()
                            .filter(q -> q.getNivel().equals(NivelTecnico.UNIVERSITARIO_INICIANTE))
                            .collect(Collectors.toList());

                    if (faceisDisponiveis.size() < quantidadeFaceis) {
                        throw new IllegalArgumentException("Não existem questões de nível FÁCIL suficientes para o conceito '" + conceito.getConceito() + "'. Cadastre mais questões correspondentes.");
                    }
                    Collections.shuffle(faceisDisponiveis);
                    questoesGeradas.addAll(faceisDisponiveis.stream().limit(quantidadeFaceis).toList());
                }

                if (quantidadeMedias > 0) {
                    List<QuestaoDTO> mediasDisponiveis = questoesPorConceito.stream()
                            .filter(q -> q.getNivel().equals(NivelTecnico.UNIVERSITARIO_INTERMEDIARIO))
                            .collect(Collectors.toList());

                    if (mediasDisponiveis.size() < quantidadeMedias) {
                        throw new IllegalArgumentException("Não existem questões de nível MÉDIO suficientes para o conceito '" + conceito.getConceito() + "'. Cadastre mais questões correspondentes.");
                    }
                    Collections.shuffle(mediasDisponiveis);
                    questoesGeradas.addAll(mediasDisponiveis.stream().limit(quantidadeMedias).toList());
                }

                if (quantidadeDificeis > 0) {
                    List<QuestaoDTO> dificeisDisponiveis = questoesPorConceito.stream()
                            .filter(q -> q.getNivel().equals(NivelTecnico.UNIVERSITARIO_AVANCADO))
                            .collect(Collectors.toList());

                    if (dificeisDisponiveis.size() < quantidadeDificeis) {
                        throw new IllegalArgumentException("Não existem questões de nível DIFÍCIL suficientes para o conceito '" + conceito.getConceito() + "'. Cadastre mais questões correspondentes.");
                    }
                    Collections.shuffle(dificeisDisponiveis);
                    questoesGeradas.addAll(dificeisDisponiveis.stream().limit(quantidadeDificeis).toList());
                }
            }
        }

        return questoesGeradas;
    }
}