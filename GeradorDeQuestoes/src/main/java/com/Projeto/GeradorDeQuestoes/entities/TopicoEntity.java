package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tb_topico")
public class TopicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    public TopicoEntity() {}

    public TopicoEntity(String nome) {
        this.nome = nome;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}