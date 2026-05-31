export interface Questao {
    id?: string;
    topico?: string;
    enunciado: string;
    alternativas: {[key:string]: string};
    respostaCorreta: string;
    nivel: string;
    conceito: string;
}