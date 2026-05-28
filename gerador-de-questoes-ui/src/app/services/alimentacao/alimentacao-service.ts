import { HttpClient, HttpEvent } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AlimentacaoService {

  private readonly API_URL = 'http://187.77.240.149:82/api/alimentacao';

  private readonly API_URL_2 = 'http://187.77.240.149:82/api/admin/material/upload/questoes';

  private readonly API_URL_3 = 'http://187.77.240.149:82/api/admin/material/upload';

  private readonly API_URL_4 = 'http://187.77.240.149:82/api/documentacao';




  constructor(private http: HttpClient) { }


  uploadPdf(file: File, topico: string, fonte: string): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);
    formData.append('topico', topico);
    formData.append('fonte', fonte);

    
    console.log("Form Data", formData);
    return this.http.post(`${this.API_URL_4}/upload`, formData, {
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    });
  }

  uploadQuestoes(file: File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post<any[]>(this.API_URL_2, formData, {
      reportProgress: true,
      observe: 'events',
    });
  }
  
}
