import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TramiteDetalle } from '../models/tramite-admin.model';
import { Funcionario } from '../models/funcionario.model';

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

  /**
   * Fetches the list of active funcionarios.
   * @returns An observable with an array of funcionarios.
   */
  getFuncionarios(): Observable<Funcionario[]> {
    return this.http.get<Funcionario[]>(
      '/api/usuarios/funcionarios/activos/combo'
    );
  }

  /**
   * Assigns a tramite to a funcionario.
   * @param numeroRadicado The ID of the tramite to assign.
   * @param funcionarioId The ID of the funcionario.
   * @returns An observable that completes on success.
   */
  asignarFuncionario(
    numeroRadicado: number,
    funcionarioId: number
  ): Observable<void> {
    return this.http.put<void>(
      `/api/tramites/${numeroRadicado}/asignar`,
      { funcionarioId }
    );
  }

}
