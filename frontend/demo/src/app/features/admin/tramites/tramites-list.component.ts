import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, EMPTY, finalize, map, of } from 'rxjs';
import { TramitesAdminService } from './data-access/tramites-admin.service';
import { TramiteDetalle } from './models/tramite-admin.model';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { MatTooltipModule } from '@angular/material/tooltip';

interface StatusPanel {
  name: string;
  tramites: TramiteDetalle[];
  isLoading: boolean;
  error: string | null;
}

@Component({
  selector: 'app-tramites-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTooltipModule,
    MatExpansionModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatMenuModule,
    MatButtonModule,
  ],
  templateUrl: './tramites-list.component.html',
  styleUrls: ['./tramites-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TramitesListComponent {
  private readonly tramitesAdminApi = inject(TramitesAdminService);

  readonly panels = signal<StatusPanel[]>([]);
  readonly expandedPanel = signal<string | null>(null);
  readonly finalStates = ['FINALIZADO', 'RECHAZADO'];

  private readonly initialPanels = toSignal(
    this.tramitesAdminApi.getEstados().pipe(
      map((statuses) =>
        statuses.map(
          (s) =>
            ({ name: s, tramites: [], isLoading: false, error: null } as StatusPanel)
        )
      )
    ),
    { initialValue: [] }
  );

  readonly funcionarios = toSignal(
    this.tramitesAdminApi.getFuncionarios().pipe(catchError(() => of([]))),
    { initialValue: [] }
  );

  constructor() {
    effect(() => {
      this.panels.set(this.initialPanels());
    });

    effect(
      () => {
        const panelName = this.expandedPanel();
        if (!panelName) return;
        const panel = this.panels().find((p) => p.name === panelName);
        // Fetch only if the panel is empty and not already loading
        if (panel && panel.tramites.length === 0 && !panel.isLoading) {
          this.fetchTramitesForPanel(panelName);
        }
      }
    );
  }

  togglePanel(panelName: string): void {
    this.expandedPanel.set(
      this.expandedPanel() === panelName ? null : panelName
    );
  }

  asignarFuncionario(
    panelName: string,
    tramite: TramiteDetalle,
    funcionarioId: number
  ): void {
    const funcionario = this.funcionarios().find((f) => f.id === funcionarioId);
    if (!funcionario) return;

    this.tramitesAdminApi
      .asignarFuncionario(tramite.numeroRadicado, funcionarioId)
      .subscribe(() => this.fetchTramitesForPanel(panelName));
  }

  cambiarEstado(
    panelName: string,
    tramite: TramiteDetalle,
    nuevoEstado: string
  ): void {
    this.tramitesAdminApi
      .cambiarEstado(tramite.numeroRadicado, nuevoEstado)
      .subscribe(() => {
        this.fetchTramitesForPanel(panelName);
        this.fetchTramitesForPanel(nuevoEstado);
      });
  }

  private fetchTramitesForPanel(panelName: string): void {
    this.panels.update((panels) =>
      panels.map((p) =>
        p.name === panelName ? { ...p, isLoading: true, error: null } : p
      )
    );

    this.tramitesAdminApi
      .getTramitesPorEstado(panelName)
      .pipe(
        map((tramites) => {
          this.panels.update((panels) =>
            panels.map((p) =>
              p.name === panelName
                ? { ...p, tramites, isLoading: false }
                : p
            )
          );
        }),
        catchError(() => {
          this.panels.update((panels) =>
            panels.map((p) =>
              p.name === panelName
                ? { ...p, error: 'Error al cargar los trámites.', isLoading: false }
                : p
            )
          );
          return EMPTY;
        })
      )
      .subscribe();
  }
}
