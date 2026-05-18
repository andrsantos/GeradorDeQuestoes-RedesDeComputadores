package com.Projeto.GeradorDeQuestoes.entities;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_documentos_referencia")
public class DocumentosReferenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nomeArquivo;
    
    private String topico;

    @JdbcTypeCode(SqlTypes.BINARY) 
    @Column(name = "arquivo_binario")
    private byte[] arquivoBinario;

    private LocalDateTime dataUpload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelTecnico nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pdf_binario_id")
    private PdfBinarioEntity pdfBinario;

    @Column(name = "fonte")
    private String fonte;

    public DocumentosReferenciaEntity() {
    }

    public String getFonte() {
        return this.fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
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



    public PdfBinarioEntity getPdfBinario() {
        return this.pdfBinario;
    }

    public void setPdfBinario(PdfBinarioEntity pdfBinario) {
        this.pdfBinario = pdfBinario;
    }


    public NivelTecnico getNivel() {
        return this.nivel;
    }

    public void setNivel(NivelTecnico nivel) {
        this.nivel = nivel;
    }


}