package com.Projeto.GeradorDeQuestoes.dto;

import java.util.Map;

import com.Projeto.GeradorDeQuestoes.enums.NivelTecnico;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class QuestaoDTO {
    
    String id;
    @JsonAlias({"enunciado", "question", "statement", "texto"})
    String enunciado;
    @JsonAlias({"alternativas", "options", "choices"})
    Map<String, String> alternativas; 
    @JsonAlias({"gabarito", "answer", "resposta", "correct_answer"})   
    String respostaCorreta;
    String explicacao;
    String conceito;
    String competencia;
    String comentarioTecnico;
    String topico;
    NivelTecnico nivel;
    private QuestaoDTO questaoInspirada;

    public QuestaoDTO() {
    }

    public QuestaoDTO(String enunciado, Map<String,String> alternativas, String respostaCorreta, String explicacao, String conceito, String competencia, String comentarioTecnico) {
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
        this.explicacao = explicacao;
        this.conceito = conceito;
        this.competencia = competencia;
        this.comentarioTecnico = comentarioTecnico;
    }

        public QuestaoDTO(String id, String enunciado, Map<String,String> alternativas, String respostaCorreta, 
        String conceito, String competencia, String comentarioTecnico, NivelTecnico nivel) {
        this.id = id;
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
        this.conceito = conceito;
        this.competencia = competencia;
        this.comentarioTecnico = comentarioTecnico;
        this.nivel = nivel;
    }
    
        public QuestaoDTO(String id, String enunciado, Map<String,String> alternativas, String respostaCorreta, 
        String conceito, String competencia, String comentarioTecnico, String topico, NivelTecnico nivel) {
        this.id = id;
        this.enunciado = enunciado;
        this.alternativas = alternativas;
        this.respostaCorreta = respostaCorreta;
        this.conceito = conceito;
        this.competencia = competencia;
        this.comentarioTecnico = comentarioTecnico;
        this.topico = topico;
        this.nivel = nivel;
    }



    public String getEnunciado() {
        return this.enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public Map<String,String> getAlternativas() {
        return this.alternativas;
    }

    public void setAlternativas(Map<String,String> alternativas) {
        this.alternativas = alternativas;
    }

    public String getRespostaCorreta() {
        return this.respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public String getExplicacao() {
        return this.explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public String getConceito() {
        return this.conceito;
    }

    public void setConceito(String conceito) {
        this.conceito = conceito;
    }

    public String getCompetencia() {
        return this.competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getComentarioTecnico() {
        return this.comentarioTecnico;
    }

    public void setComentarioTecnico(String comentarioTecnico) {
        this.comentarioTecnico = comentarioTecnico;
    }

    public String getTopico() {
        return this.topico;
    }

    public void setTopico(String topico) {
        this.topico = topico;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public QuestaoDTO getQuestaoInspirada() {
        return this.questaoInspirada;
    }

    public void setQuestaoInspirada(QuestaoDTO questaoInspirada) {
        this.questaoInspirada = questaoInspirada;
    }


    public NivelTecnico getNivel() {
        return this.nivel;
    }

    public void setNivel(NivelTecnico nivel) {
        this.nivel = nivel;
    }
    

}
