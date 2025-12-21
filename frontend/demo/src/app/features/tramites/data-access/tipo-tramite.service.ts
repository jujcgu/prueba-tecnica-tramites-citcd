import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoTramiteDto } from '../models/tipo-tramite.dto';

@Injectable({
  providedIn: 'root'
})
export class TipoTramiteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/tipos-tramite';

  getActivos(): Observable<TipoTramiteDto[]> {
    const params = new HttpParams().set('activos', 'true');
    return this.http.get<TipoTramiteDto[]>(this.apiUrl, { params });
  }
}
