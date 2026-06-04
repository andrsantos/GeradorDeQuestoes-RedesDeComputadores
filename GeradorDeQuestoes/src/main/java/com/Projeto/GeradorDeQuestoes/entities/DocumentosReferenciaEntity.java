package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_documentos_referencia")
public class DocumentosReferenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fonte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id", nullable = false)
    private TopicoEntity topico;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pdf_binario_id", referencedColumnName = "id")
    private PdfBinarioEntity pdfBinario; 

    public DocumentosReferenciaEntity() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public TopicoEntity getTopico() {
        return topico;
    }

    public void setTopico(TopicoEntity topico) {
        this.topico = topico;
    }

    public PdfBinarioEntity getPdfBinario() {
        return pdfBinario;
    }

    public void setPdfBinario(PdfBinarioEntity pdfBinario) {
        this.pdfBinario = pdfBinario;
    }
}