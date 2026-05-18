export interface QuestaoFormatoAvaliarDTO {
 numeroQuestao: number;
 enunciado: string;
 respostaCorreta: string;
 alternativas: {[key:string]: string};
}