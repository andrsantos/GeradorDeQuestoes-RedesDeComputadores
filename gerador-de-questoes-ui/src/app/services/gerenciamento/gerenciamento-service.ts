import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Cenario } from '../../models/cenario.model';
import { GerenciamentoRequest } from '../../models/gerenciamento-request.model';
import { Prompt } from '../../models/prompt.model';
import { Documento } from '../../models/documento.model';
import { DocumentoExibicao } from '../../models/documento-exibicao.model';

@Injectable({
  providedIn: 'root',
})
export class GerenciamentoService {

  private readonly API_URL = 'http://187.77.240.149:82/api/gerenciamento';
  private readonly API_URL_2 = 'http://187.77.240.149:82/api/gerenciamento/listar/documentos';
  private readonly API_URL_3 = 'http://187.77.240.149:82/api/gerenciamento/listar/documentos/filtrados';
  private readonly API_URL_4 = 'http://187.77.240.149:82/api/documentacao/download';



  constructor(private http: HttpClient) {}
  

  /* ####### AÇÕES CRUD PARA OS CENÁRIOS ####### */
  listarCenarios(request: GerenciamentoRequest): Observable<Cenario[]>{
   return this.http.post<Cenario[]>(`${this.API_URL}/listar`, request);
  }

  inserirCenario(cenario: Cenario): Observable<Cenario> {
    return this.http.post<Cenario>(`${this.API_URL}/criar/cenario`, cenario);
  }

  deletarCenario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/deletar/cenario/${id}`);
  }

  atualizarCenario(id: number, cenario: Cenario): Observable<Cenario> {
    return this.http.put<Cenario>(`${this.API_URL}/atualizar/cenario/${cenario.id}`, cenario);
  }

  /* ####### AÇÕES CRUD PARA OS PROMPTS ####### */
  listarPrompts(request: GerenciamentoRequest): Observable<Prompt[]>{
   return this.http.post<Prompt[]>(`${this.API_URL}/listar`, request);
  }

  inserirPrompt(prompt: Prompt): Observable<Prompt> {
    return this.http.post<Prompt>(`${this.API_URL}/criar/prompt`, prompt);
  }

  deletarPrompt(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/deletar/prompt/${id}`);
  }

  atualizarPrompt(id: string, prompt: Prompt): Observable<Prompt> {
    return this.http.put<Prompt>(`${this.API_URL}/atualizar/prompt/${prompt.id}`, prompt);
  }

  /* ####### AÇÕES CRUD PARA A DOCUMENTAÇÃO ####### */
  listarDocumentos(): Observable<Documento[]>{
   return this.http.get<Documento[]>(`${this.API_URL_2}`);
  }

  listarDocumentosFiltrados(): Observable<DocumentoExibicao[]>{
   return this.http.get<DocumentoExibicao[]>(`${this.API_URL_3}`);
  }

  baixarMaterialBinario(idBinario: string): Observable<Blob> {
    const urlDownload = `http://187.77.240.149:82/api/documentacao/download/${idBinario}`; 
    return this.http.get(urlDownload, { 
      responseType: 'blob' 
    });
  }

  deletarMaterialBinario(idBinario: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/deletar/documento/${idBinario}`, {
      responseType: 'text'
    });
  }



}
