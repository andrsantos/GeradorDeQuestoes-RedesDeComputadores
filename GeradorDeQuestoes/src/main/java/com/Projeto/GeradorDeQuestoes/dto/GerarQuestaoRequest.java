package com.Projeto.GeradorDeQuestoes.dto;

import java.util.List;

public record GerarQuestaoRequest(
    List<TopicoQuantidade> topicos
) {}