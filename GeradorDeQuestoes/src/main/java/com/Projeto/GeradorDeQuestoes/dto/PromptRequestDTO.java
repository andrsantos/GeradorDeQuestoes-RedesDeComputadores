package com.Projeto.GeradorDeQuestoes.dto;


public class PromptRequestDTO {

    String topicoNome;
    
    String nivel; 

    String instrucao;
    
    boolean ativo;    



    public PromptRequestDTO() {
    }
    
    public String getTopicoNome() {
        return this.topicoNome;
    }

    public void setTopicoNome(String topicoNome) {
        this.topicoNome = topicoNome;
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

}
