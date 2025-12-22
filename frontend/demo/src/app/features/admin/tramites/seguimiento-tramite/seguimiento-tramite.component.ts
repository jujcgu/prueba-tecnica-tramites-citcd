import {ChangeDetectionStrategy, Component, inject, input} from '@angular/core';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {TramitesAdminService} from '../data-access/tramites-admin.service';
import {catchError, of, switchMap} from 'rxjs';
import {CommonModule} from '@angular/common';
import {MatIconModule} from '@angular/material/icon';
import {RouterModule} from '@angular/router';
import {MatButtonModule} from '@angular/material/button';
import {Seguimiento} from '../models/seguimiento.model';

@Component({
  selector: 'app-seguimiento-tramite',
  standalone: true,
  imports: [CommonModule, MatIconModule, RouterModule, MatButtonModule],
  templateUrl: './seguimiento-tramite.component.html',
  styleUrls: ['./seguimiento-tramite.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeguimientoTramiteComponent {
  private readonly tramitesAdminApi = inject(TramitesAdminService);

  // Change input to a string to make router binding more reliable
  readonly numeroRadicado = input.required<string>();

  private readonly seguimiento$ = toObservable(this.numeroRadicado).pipe(
    switchMap(radicadoStr => {
      // Manually convert the string from the URL to a number
      const radicadoNum = Number(radicadoStr);

      // If the URL param isn't a valid number, return empty results
      if (isNaN(radicadoNum)) {
        return of([] as Seguimiento[]);
      }

      // Fetch data using the converted number
      return this.tramitesAdminApi.getSeguimiento(radicadoNum).pipe(
        catchError(() => of([] as Seguimiento[]))
      );
    })
  );

  // This signal now depends on the more robust logic above
  readonly seguimiento = toSignal(this.seguimiento$);

  protected getIconForEvent(tipoEvento: string): string {
    switch (tipoEvento) {
      case 'CREACION':
        return 'add_circle_outline';
      case 'ASIGNACION':
        return 'assignment_ind';
      case 'CAMBIO_ESTADO':
        return 'swap_horiz';
      case 'COMENTARIO':
        return 'comment';
      default:
        return 'info_outline';
    }
  }

  protected getEventDescription(event: Seguimiento): string {
    switch (event.tipoEvento) {
      case 'CREACION':
        return 'Trámite radicado en el sistema.';
      case 'ASIGNACION':
        return `Funcionario asignado al trámite.`;
      case 'CAMBIO_ESTADO':
        return `Cambio de estado: de '${event.ultimoEstado || 'N/A'}' a '${event.nuevoEstado}'.`;
      case 'COMENTARIO':
        return 'Se agregó un nuevo comentario.';
      default:
        return 'Evento desconocido.';
    }
  }
}
