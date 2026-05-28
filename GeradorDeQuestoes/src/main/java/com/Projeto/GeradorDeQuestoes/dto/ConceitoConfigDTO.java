package com.Projeto.GeradorDeQuestoes.dto;

public class ConceitoConfigDTO {

    private String conceito;
    private int quantidadeFaceis;
    private int quantidadeMedias;
    private int quantidadeDificeis; 
    private int quantidade;

    public ConceitoConfigDTO() {}


    public ConceitoConfigDTO(String conceito, 
        String nivel,
        int quantidadeFaceis,
        int quantidadeMedias,
        int quantidadeDificeis,
        int quantidade) {
        this.conceito = conceito;
        this.quantidade = quantidade;
        this.quantidadeFaceis = quantidadeFaceis;
        this.quantidadeMedias = quantidadeMedias;
        this.quantidadeDificeis = quantidadeDificeis;
    }


    public String getConceito() {
        return this.conceito;
    }

    public void setConceito(String conceito) {
        this.conceito = conceito;
    }
    

    public int getQuantidadeFaceis() {
        return this.quantidadeFaceis;
    }

    public void setQuantidadeFaceis(int quantidadeFaceis) {
        this.quantidadeFaceis = quantidadeFaceis;
    }

    public int getQuantidadeMedias() {
        return this.quantidadeMedias;
    }

    public void setQuantidadeMedias(int quantidadeMedias) {
        this.quantidadeMedias = quantidadeMedias;
    }

    public int getQuantidadeDificeis() {
        return this.quantidadeDificeis;
    }

    public void setQuantidadeDificeis(int quantidadeDificeis) {
        this.quantidadeDificeis = quantidadeDificeis;
    }

    public int getQuantidade() {
        return this.quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }


}