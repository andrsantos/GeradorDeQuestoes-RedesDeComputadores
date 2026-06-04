package com.Projeto.GeradorDeQuestoes.dto;

import java.util.UUID;

public class MaterialDTO {
    private UUID idBinario;
    private String fonte;
    private String nomeArquivo;

    public UUID getIdBinario() {
        return idBinario;
    }

    public void setIdBinario(UUID idBinario) {
        this.idBinario = idBinario;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
}