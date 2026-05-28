package com.Projeto.GeradorDeQuestoes.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

@Entity
@Table(name = "tb_provas_questoes")
public class QuestaoProvaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    private String id; 

    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prova_id", nullable = false)
    private ProvaEntity prova;

    @Column(name = "enunciado", columnDefinition = "TEXT")
    private String enunciado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "alternativas", columnDefinition = "jsonb")
    private Map<String, String> alternativas;

    @Column(name = "resposta_correta")
    private String respostaCorreta;

    @Column(name = "topico")
    private String topico;

    @Column(name = "conceito")
    private String conceito;

    @Column(name = "nivel")
    private String nivel;
    
    @Column(name = "competencia")
    private String competencia;

    @Column(name = "comentario_tecnico", columnDefinition = "TEXT")
    private String comentarioTecnico;

    public QuestaoProvaEntity() {
    }



    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    public ProvaEntity getProva() { return prova; }
    public void setProva(ProvaEntity prova) { this.prova = prova; }

    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }

    public Map<String, String> getAlternativas() { return alternativas; }
    public void setAlternativas(Map<String, String> alternativas) { this.alternativas = alternativas; }

    public String getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(String respostaCorreta) { this.respostaCorreta = respostaCorreta; }

    public String getTopico() { return topico; }
    public void setTopico(String topico) { this.topico = topico; }

    public String getConceito() { return conceito; }
    public void setConceito(String conceito) { this.conceito = conceito; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) { this.competencia = competencia; }

    public String getComentarioTecnico() { return comentarioTecnico; }
    public void setComentarioTecnico(String comentarioTecnico) { this.comentarioTecnico = comentarioTecnico; }
}