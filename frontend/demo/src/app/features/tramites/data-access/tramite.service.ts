import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CrearTramiteRequest } from '../models/crear-tramite-request.model';

@Injectable({
  providedIn: 'root'
})
export class TramiteService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/tramites';

  create(payload: CrearTramiteRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, payload);
  }
}
