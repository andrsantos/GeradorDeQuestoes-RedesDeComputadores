package com.Projeto.GeradorDeQuestoes.dto;

import java.util.Map;

public class QuestaoFormatoAvaliarDTO {
    private int numeroQuestao;
    private String enunciado;
    private String respostaCorreta;
    private Map<String, String> alternativas;


    public QuestaoFormatoAvaliarDTO() {
    }


    public QuestaoFormatoAvaliarDTO(int numeroQuestao, String enunciado, String respostaCorreta, Map<String,String> alternativas) {
        this.numeroQuestao = numeroQuestao;
        this.enunciado = enunciado;
        this.respostaCorreta = respostaCorreta;
        this.alternativas = alternativas;
    }



    public int getNumeroQuestao() {
        return this.numeroQuestao;
    }

    public void setNumeroQuestao(int numeroQuestao) {
        this.numeroQuestao = numeroQuestao;
    }

    public String getEnunciado() {
        return this.enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getRespostaCorreta() {
        return this.respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public Map<String,String> getAlternativas() {
        return this.alternativas;
    }

    public void setAlternativas(Map<String,String> alternativas) {
        this.alternativas = alternativas;
    }


}