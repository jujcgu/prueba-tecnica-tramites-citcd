import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentoRequerido } from '../models/documento-requerido.model'; // Importación limpia

@Injectable({
  providedIn: 'root'
})
export class RequisitoService {
  private readonly http = inject(HttpClient);

  // Es buena práctica definir la base de la URL como una constante privada
  private readonly baseUrl = '/api/tipos-tramite';

  /**
   * La guía recomienda tipar el retorno para que el componente
   * que lo consuma sepa exactamente qué esperar.
   */
  getDocumentosRequeridos(tipoTramiteId: number): Observable<DocumentoRequerido[]> {
    return this.http.get<DocumentoRequerido[]>(
      `${this.baseUrl}/${tipoTramiteId}/documentos-requeridos`
    );
  }
}
