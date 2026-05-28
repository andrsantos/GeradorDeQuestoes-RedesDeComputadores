package com.Projeto.GeradorDeQuestoes.services;

import java.util.List;

import com.Projeto.GeradorDeQuestoes.dto.GerarQuestaoRequest;
import com.Projeto.GeradorDeQuestoes.dto.ListaQuestoes;

public interface GeradorQuestaoService {
    ListaQuestoes gerarQuestoes(GerarQuestaoRequest request);
    List<String> extrairConceitosUnicos(String contexto,int qtd);
}