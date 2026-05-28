package com.Projeto.GeradorDeQuestoes.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TopicoConfigDTO {

    private String id;
    private String topico;
    private String nivel;
    private String instrucoesEspecificas;
    private LocalDateTime dataAtualizacao;
    private List<UUID> listaDocumentos;


    public TopicoConfigDTO() {
    }

    public TopicoConfigDTO(String topico, String nivel, String instrucoesEspecificas) {
        this.topico = topico;
        this.nivel = nivel;
        this.instrucoesEspecificas = instrucoesEspecificas;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public String getNivel() {
        return this.nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getInstrucoesEspecificas() {
        return this.instrucoesEspecificas;
    }

    public void setInstrucoesEspecificas(String instrucoesEspecificas) {
        this.instrucoesEspecificas = instrucoesEspecificas;
    }

    public List<UUID> getListaDocumentos() {
        return this.listaDocumentos;
    }

    public void setListaDocumentos(List<UUID> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }


    public LocalDateTime getDataAtualizacao() {
        return this.dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
     

    
}
