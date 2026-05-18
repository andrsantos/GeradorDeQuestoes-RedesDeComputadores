import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Prova } from '../../models/prova.model';
import { GerarQuestaoRequest } from '../../models/gerar-questao-request.model';
import { ProvaInfo } from '../../models/prova-info.model';
import { ProvaSalva } from '../../models/prova-entity.model';
import { TopicoQuantidade } from '../../models/topico-quantidade.model';
import { Questao } from '../../models/questao.model';

@Injectable({
  providedIn: 'root',
})
export class ProvaService {

  private readonly API_URL = 'http://187.77.240.149:82/api/provas';
  private readonly API_URL_SALVAS = 'http://187.77.240.149:82/api/provas-salvas';
  private readonly API_URL_TOPICOS = 'http://187.77.240.149:82/api/topicos'; 
  private readonly API_QUESTOES_GERAR = 'http://187.77.240.149:82/api/questoes/gerar'; 

  constructor(private http: HttpClient) {}

  criarProva(): Observable<Prova> {
    return this.http.post<Prova>(this.API_URL, {});
  }

  getProva(id: string): Observable<Prova> {
    return this.http.get<Prova>(`${this.API_URL}/${id}`);
  }

  atualizarQuestaoExistente(id: string, questao: Questao): Observable<any> {
    return this.http.put(`${this.API_URL_SALVAS}/questoes/${id}`, questao);
  }

  adicionarQuestoes(id: string, request: GerarQuestaoRequest): Observable<Prova> {
    return this.http.post<Prova>(`${this.API_URL}/${id}/questoes`, request);
  }

  gerarProvaBanco(id: string, topicos: TopicoQuantidade[]): Observable<Prova> {
  const request = { topicos: topicos };
  console.log("Request gerarProvaBanco:", request);
  return this.http.post<Prova>(`${this.API_URL}/${id}/prova-banco`, request);
  }

  adicionarQuestoesAutomatico(id: string, topicos: TopicoQuantidade[]): Observable<Prova> {
    const request = { topicos: topicos };
    console.log("Request adicionarQuestoesAutomatico:", request);
    return this.http.post<Prova>(`${this.API_URL}/${id}/questoes-automaticas`, request);
  }

  descartarQuestao(id: string, indice: number): Observable<Prova> {
    const params = new HttpParams().set('indice', indice.toString());
    return this.http.delete<Prova>(`${this.API_URL}/${id}/questoes`, { params });
  }

  finalizarProvaPdf(id: string): Observable<Blob> {
    return this.http.post(`${this.API_URL}/${id}/finalizar-pdf`, {}, {
      responseType: 'blob' 
    });
  }

  getProvasSalvas(): Observable<ProvaInfo[]> {
    return this.http.get<ProvaInfo[]>(this.API_URL_SALVAS);
  }

  getDetalheProva(id: string): Observable<ProvaSalva> {
    return this.http.get<ProvaSalva>(`${this.API_URL_SALVAS}/${id}`);
  }

  deleteProva(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL_SALVAS}/${id}`);
  }

  downloadProvaSalvaPdf(id: string): Observable<Blob> {
    return this.http.get(`${this.API_URL_SALVAS}/${id}/download-pdf`, {
      responseType: 'blob' 
    });
  }

  getTopicosDisponiveis(): Observable<string[]> {
    return this.http.get<string[]>(this.API_URL_TOPICOS);
  }


  gerarQuestaoAvulsa(topico: string, quantidade: number = 1): Observable<any> {
    return this.http.post<any>(this.API_QUESTOES_GERAR, { 
      topico: topico, 
      quantidade: quantidade 
    });
  }


  salvarProvaManual(id: string, questoes: Questao[]): Observable<Prova> {
    return this.http.post<Prova>(`${this.API_URL}/${id}/manual`, questoes);
  }

  
}
