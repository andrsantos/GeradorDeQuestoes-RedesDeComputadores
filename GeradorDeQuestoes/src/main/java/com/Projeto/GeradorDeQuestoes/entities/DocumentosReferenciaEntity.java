package com.Projeto.GeradorDeQuestoes.entities;

import java.util.*;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_documentos_referencia")
public class DocumentosReferenciaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String topico;

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

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public PdfBinarioEntity getPdfBinario() {
        return this.pdfBinario;
    }

    public void setPdfBinario(PdfBinarioEntity pdfBinario) {
        this.pdfBinario = pdfBinario;
    }



}