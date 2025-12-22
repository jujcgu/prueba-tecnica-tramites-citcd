import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentoRequerido } from '../models/documento-requerido.model';

@Injectable({
  providedIn: 'root'
})
export class RequisitoService {
  private readonly http = inject(HttpClient);

  private readonly baseUrl = '/api/tipos-tramite';

  getDocumentosRequeridos(tipoTramiteId: number): Observable<DocumentoRequerido[]> {
    return this.http.get<DocumentoRequerido[]>(
      `${this.baseUrl}/${tipoTramiteId}/documentos-requeridos`
    );
  }
}
