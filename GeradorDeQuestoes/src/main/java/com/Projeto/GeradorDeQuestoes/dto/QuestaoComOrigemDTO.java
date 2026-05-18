package com.Projeto.GeradorDeQuestoes.dto;

import java.util.Map;

public class QuestaoComOrigemDTO {
    private String topico;
    private String enunciado;
    private String tipo;
    private Map<String, String> alternativas;
    private String respostaCorreta;
    private String conceito;
    private String comentarioTecnico;
    private String competencia;
    private String nivel;
    private String dataCriacao;
    private String origem; 

    public QuestaoComOrigemDTO(){
        
    }

    public String getTopico() { return topico; }
    public void setTopico(String topico) { this.topico = topico; }
    public String getEnunciado() { return enunciado; }
    public void setEnunciado(String enunciado) { this.enunciado = enunciado; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Map<String, String> getAlternativas() { return alternativas; }
    public void setAlternativas(Map<String, String> alternativas) { this.alternativas = alternativas; }
    public String getRespostaCorreta() { return respostaCorreta; }
    public void setRespostaCorreta(String respostaCorreta) { this.respostaCorreta = respostaCorreta; }
    public String getConceito() { return conceito; }
    public void setConceito(String conceito) { this.conceito = conceito; }
    public String getComentarioTecnico() { return comentarioTecnico; }
    public void setComentarioTecnico(String comentarioTecnico) { this.comentarioTecnico = comentarioTecnico; }
    public String getCompetencia() { return competencia; }
    public void setCompetencia(String competencia) { this.competencia = competencia; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
}