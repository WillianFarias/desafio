import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio } from '../models/beneficio.model';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {

  private readonly API = 'http://localhost:8080/api/beneficios';

  constructor(private http: HttpClient) { }

  listarTodos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.API);
  }

  transferir(fromId: number, toId: number, amount: number): Observable<string> {
    const params = new HttpParams()
      .set('fromId', fromId.toString())
      .set('toId', toId.toString())
      .set('amount', amount.toString());

    return this.http.post(this.API + '/transferir', null, { params, responseType: 'text' });
  }
}
