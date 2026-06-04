package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_prompts")
public class PromptEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topico_id")
    private TopicoEntity topico; 

    @Column(name = "nivel")
    private String nivel; 

    @Column(name = "instrucao", columnDefinition = "TEXT")
    private String instrucao;

    @Column(name = "ativo")
    private boolean ativo;



    public PromptEntity() {
    }



    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TopicoEntity getTopico() {
        return this.topico;
    }

    public void setTopico(TopicoEntity topico) {
        this.topico = topico;
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