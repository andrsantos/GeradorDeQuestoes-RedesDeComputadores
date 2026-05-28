package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tb_provas")
public class ProvaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "data_criacao", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private OffsetDateTime dataCriacao;
    

    @JsonManagedReference
    @OneToMany(
        mappedBy = "prova",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<QuestaoProvaEntity> questoes = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public OffsetDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(OffsetDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public List<QuestaoProvaEntity> getQuestoes() { return questoes; }
    public void setQuestoes(List<QuestaoProvaEntity> questoes) { this.questoes = questoes; }

    public void addQuestao(QuestaoProvaEntity questao) {
        questoes.add(questao);
        questao.setProva(this);
    }
}
