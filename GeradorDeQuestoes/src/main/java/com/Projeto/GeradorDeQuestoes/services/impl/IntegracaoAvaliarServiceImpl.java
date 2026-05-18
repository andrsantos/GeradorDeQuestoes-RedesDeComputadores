package com.Projeto.GeradorDeQuestoes.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Projeto.GeradorDeQuestoes.dto.QuestaoFormatoAvaliarDTO;
import com.Projeto.GeradorDeQuestoes.services.IntegracaoAvaliarService;


@Service
public class IntegracaoAvaliarServiceImpl implements IntegracaoAvaliarService {

    @Override
    public String converterParaFormatoAvaliar(List<QuestaoFormatoAvaliarDTO> questoes) {

    StringBuilder sb = new StringBuilder();

    for (QuestaoFormatoAvaliarDTO q : questoes) {
        sb.append("Q").append(q.getNumeroQuestao()).append(": ").append(q.getEnunciado()).append("\n");

        q.getAlternativas().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                String letra = entry.getKey().toLowerCase();
                String texto = entry.getValue();
                
                sb.append(letra).append(") ").append(texto);

                if (letra.equals(q.getRespostaCorreta().toLowerCase())) {
                    sb.append(" *");
                }
                sb.append("\n");
            });

        sb.append("\n");
    }

        return sb.toString();
    }
    
}
