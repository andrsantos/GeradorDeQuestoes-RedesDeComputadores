package com.Projeto.GeradorDeQuestoes.dto;

public class PromptResponseDTO {
    
    String id;
    String topicoId;
    String nivel;
    String instrucao;
    boolean ativo;



    public PromptResponseDTO() {
    }


    public PromptResponseDTO(String topicoId, String nivel, String instrucao, boolean ativo) {
        this.topicoId = topicoId;
        this.nivel = nivel;
        this.instrucao = instrucao;
        this.ativo = ativo;
    }

    public PromptResponseDTO(String id, String topicoId, String nivel, String instrucao, boolean ativo) {
        this.id = id;
        this.topicoId = topicoId;
        this.nivel = nivel;
        this.instrucao = instrucao;
        this.ativo = ativo;
    }




    public String getTopicoId() {
        return this.topicoId;
    }

    public void setTopicoId(String topicoId) {
        this.topicoId = topicoId;
    }
     
    public String getNivel() {
        return this.nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getInstrucao() {
        return this.instrucao;
    }

    public void setInstrucao(String instrucao) {
        this.instrucao = instrucao;
    }

    public boolean isAtivo() {
        return this.ativo;
    }

    public boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}