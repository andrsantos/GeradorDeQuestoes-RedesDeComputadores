import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Prompt } from '../../models/prompt.model'; 

@Injectable({
  providedIn: 'root',
})
export class PromptService {
  
  private apiUrl = 'http://187.77.240.149:83/api/prompts';

  constructor(private http: HttpClient) {}

  cadastrarPrompt(prompt: Prompt): Observable<Prompt> {
    return this.http.post<Prompt>(this.apiUrl, prompt);
  }

 
  listarPromptsPorTopico(nomeTopico: string): Observable<Prompt[]> {
    return this.http.get<Prompt[]>(`${this.apiUrl}/topico/${nomeTopico}`);
  }

 
  listarTodos(): Observable<Prompt[]> {
    return this.http.get<Prompt[]>(this.apiUrl);
  }


  atualizarPrompt(id: string, prompt: Prompt): Observable<Prompt> {
    return this.http.put<Prompt>(`${this.apiUrl}/${id}`, prompt);
  }


  deletarPrompt(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  alternarStatusPrompt(id: string): Observable<Prompt> {
    return this.http.patch<Prompt>(`${this.apiUrl}/${id}/status`, {});
  }

  editarPrompt(id: string, promptData: any): Observable<Prompt> {
    return this.http.put<Prompt>(`${this.apiUrl}/${id}`, promptData);
  }

  


}