package com.Projeto.GeradorDeQuestoes.dto;

import java.util.List;

public record ResultadoIngestaoDTO(
        int chunksInseridos,
        List<String> conceitosGlobais
) {}