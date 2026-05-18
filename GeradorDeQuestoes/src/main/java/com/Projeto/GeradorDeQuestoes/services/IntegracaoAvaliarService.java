package com.Projeto.GeradorDeQuestoes.services;

import java.util.List;

import com.Projeto.GeradorDeQuestoes.dto.QuestaoFormatoAvaliarDTO;

public interface IntegracaoAvaliarService {

  String converterParaFormatoAvaliar(List<QuestaoFormatoAvaliarDTO> questoes);   
    
}
