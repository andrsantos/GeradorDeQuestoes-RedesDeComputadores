package com.Projeto.GeradorDeQuestoes.dto;

import java.util.List;

public class TopicoQuantidade{

    String topico; // Camada de Aplicação
    List<ConceitoConfigDTO> subtopicos; // Socket, Arquitetura P2P
    int quantidade; // 3
    int quantidadeDificeis; // 1 
    int quantidadeMedias; // 1
    int quantidadeFaceis; // 1
    String diretrizCustomizada; // null

    public String getTopico() {
        return this.topico;
    }
    public void setTopico(String topico) {
        this.topico = topico;
    }
    public int getQuantidade() {
        return this.quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getQuantidadeDificeis() {
        return this.quantidadeDificeis;
    }

    public void setQuantidadeDificeis(int quantidadeDificeis) {
        this.quantidadeDificeis = quantidadeDificeis;
    }

    public int getQuantidadeMedias() {
        return this.quantidadeMedias;
    }

    public void setQuantidadeMedias(int quantidadeMedias) {
        this.quantidadeMedias = quantidadeMedias;
    }

    public int getQuantidadeFaceis() {
        return this.quantidadeFaceis;
    }

    public void setQuantidadeFaceis(int quantidadeFaceis) {
        this.quantidadeFaceis = quantidadeFaceis;
    }


    public List<ConceitoConfigDTO> getSubtopicos() {
        return this.subtopicos;
    }

    public void setSubtopicos(List<ConceitoConfigDTO> subtopicos) {
        this.subtopicos = subtopicos;
    }
    

    public String getDiretrizCustomizada() {
        return this.diretrizCustomizada;
    }

    public void setDiretrizCustomizada(String diretrizCustomizada) {
        this.diretrizCustomizada = diretrizCustomizada;
    }



}
