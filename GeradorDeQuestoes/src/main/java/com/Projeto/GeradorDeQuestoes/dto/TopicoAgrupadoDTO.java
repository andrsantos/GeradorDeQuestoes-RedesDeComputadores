package com.Projeto.GeradorDeQuestoes.dto;

import java.util.List;

public class TopicoAgrupadoDTO {
    private String topicoNome;
    private List<MaterialDTO> materiais;

    public String getTopicoNome() {
        return topicoNome;
    }

    public void setTopicoNome(String topicoNome) {
        this.topicoNome = topicoNome;
    }

    public List<MaterialDTO> getMateriais() {
        return materiais;
    }

    public void setMateriais(List<MaterialDTO> materiais) {
        this.materiais = materiais;
    }
}