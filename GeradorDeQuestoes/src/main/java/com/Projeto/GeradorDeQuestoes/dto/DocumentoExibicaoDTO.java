package com.Projeto.GeradorDeQuestoes.dto;

import java.util.UUID;

public class DocumentoExibicaoDTO {

    private UUID idReferencia; 
    private UUID idBinario;    
    private String topico;
    private String fonte;
    private String materialReferencia;

    public DocumentoExibicaoDTO(UUID idReferencia, UUID idBinario, String topico, String fonte, String materialReferencia) {
        this.idReferencia = idReferencia;
        this.idBinario = idBinario;
        this.topico = topico;
        this.fonte = fonte;
        this.materialReferencia = materialReferencia;
    }

    public DocumentoExibicaoDTO() {
    }

    public UUID getIdReferencia() {
        return this.idReferencia;
    }

    public void setIdReferencia(UUID idReferencia) {
        this.idReferencia = idReferencia;
    }

    public UUID getIdBinario() {
        return this.idBinario;
    }

    public void setIdBinario(UUID idBinario) {
        this.idBinario = idBinario;
    }

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public String getFonte() {
        return this.fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getMaterialReferencia() {
        return this.materialReferencia;
    }

    public void setMaterialReferencia(String materialReferencia) {
        this.materialReferencia = materialReferencia;
    }

    
}