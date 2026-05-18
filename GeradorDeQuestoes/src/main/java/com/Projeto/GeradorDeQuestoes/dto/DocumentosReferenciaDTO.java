package com.Projeto.GeradorDeQuestoes.dto;

import java.time.LocalDateTime;


public class DocumentosReferenciaDTO {

    private String nomeArquivo;
    
    private String topico;
    
    private byte[] arquivoBinario;

    private LocalDateTime dataUpload;


    public DocumentosReferenciaDTO() {
    }

    public DocumentosReferenciaDTO(String nomeArquivo, String topico, byte[] arquivoBinario, LocalDateTime dataUpload) {
        this.nomeArquivo = nomeArquivo;
        this.topico = topico;
        this.arquivoBinario = arquivoBinario;
        this.dataUpload = dataUpload;
    }

    public String getNomeArquivo() {
        return this.nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public byte[] getArquivoBinario() {
        return this.arquivoBinario;
    }

    public void setArquivoBinario(byte[] arquivoBinario) {
        this.arquivoBinario = arquivoBinario;
    }

    public LocalDateTime getDataUpload() {
        return this.dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }


    
}
