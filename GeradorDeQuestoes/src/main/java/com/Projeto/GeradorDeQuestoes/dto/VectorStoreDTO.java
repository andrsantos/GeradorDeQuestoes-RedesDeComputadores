package com.Projeto.GeradorDeQuestoes.dto;

public class VectorStoreDTO {

    private String conteudo;

    private String metadata;

    

    public VectorStoreDTO() {
    }
    

    public VectorStoreDTO(String conteudo, String metadata) {
        this.conteudo = conteudo;
        this.metadata = metadata;
    }


    public String getConteudo() {
        return this.conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getMetadata() {
        return this.metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }


    
}
