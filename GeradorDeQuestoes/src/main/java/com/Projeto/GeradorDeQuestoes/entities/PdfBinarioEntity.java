package com.Projeto.GeradorDeQuestoes.entities;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_pdf_binario")
public class PdfBinarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nomeOriginal;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "arquivo_binario")
    private byte[] arquivoBinario;

    private LocalDateTime dataUpload;


    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeOriginal() {
        return this.nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
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