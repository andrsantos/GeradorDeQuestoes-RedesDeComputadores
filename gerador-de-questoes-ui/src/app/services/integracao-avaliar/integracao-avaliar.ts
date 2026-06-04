import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { QuestaoFormatoAvaliarDTO } from '../../models/questao-formato-avaliar.model';

@Injectable({
  providedIn: 'root',
})
export class IntegracaoAvaliarService {

  private API_URL = 'http://187.77.240.149:83/api/integracao/avaliar';

  constructor(private http: HttpClient) {}

  exportarFormatoAvaliar(provaFormatoAvaliar: QuestaoFormatoAvaliarDTO[]): any {
   return this.http.post(`${this.API_URL}/exportar`, provaFormatoAvaliar, {
    responseType: 'text'
   });

}


}