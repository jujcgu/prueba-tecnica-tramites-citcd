import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TramiteDetalle } from '../models/tramite-admin.model';

@Injectable({
  providedIn: 'root',
})
export class TramitesAdminService {
  private readonly http = inject(HttpClient);

  /**
   * Fetches the available status options for procedures.
   * @returns An observable with an array of status strings.
   */
  getEstados(): Observable<string[]> {
    return this.http.get<string[]>('/api/tramites/estados');
  }

  /**
   * Fetches the detailed list of procedures for a given status.
   * @param estado The status to filter by.
   * @returns An observable with an array of procedure details.
   */
  getTramitesPorEstado(estado: string): Observable<TramiteDetalle[]> {
    return this.http.get<TramiteDetalle[]>(
      `/api/tramites/estado/${estado}/detalle`
    );
  }
}
