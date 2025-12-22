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
import {MatSelectModule} from '@angular/material/select';
import {MatFormFieldModule} from '@angular/material/form-field';

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
    MatExpansionModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
  ],
  templateUrl: './tramites-list.component.html',
  styleUrls: ['./tramites-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TramitesListComponent {
  private readonly tramitesAdminApi = inject(TramitesAdminService);

  // --- State Signals ---
  readonly panels = signal<StatusPanel[]>([]);
  readonly expandedPanel = signal<string | null>(null);

  // --- Data Signals from APIs ---
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
        if (panel && panel.tramites.length === 0 && !panel.isLoading && !panel.error) {
          this.fetchTramitesForPanel(panelName);
        }
      },
      { allowSignalWrites: true }
    );
  }

  togglePanel(panelName: string): void {
    this.expandedPanel.set(
      this.expandedPanel() === panelName ? null : panelName
    );
  }

  /**
   * Assigns a funcionario to a tramite and then refetches the data for the panel.
   * @param panelName The name of the panel containing the tramite.
   * @param tramite The tramite to be updated.
   * @param funcionarioId The ID of the selected funcionario.
   */
  asignarFuncionario(
    panelName: string,
    tramite: TramiteDetalle,
    funcionarioId: number
  ): void {
    const funcionario = this.funcionarios().find((f) => f.id === funcionarioId);
    if (!funcionario) return;

    this.tramitesAdminApi
      .asignarFuncionario(tramite.numeroRadicado, funcionarioId)
      .subscribe(() => {
        // After successful assignment, refetch the entire panel's data
        // to ensure the view is consistent with the backend.
        this.fetchTramitesForPanel(panelName);
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
        catchError(() => {
          this.panels.update((panels) =>
            panels.map((p) =>
              p.name === panelName
                ? { ...p, error: 'Error al cargar los trámites. Intente de nuevo.' }
                : p
            )
          );
          return EMPTY;
        }),
        finalize(() => {
          this.panels.update((panels) =>
            panels.map((p) =>
              p.name === panelName ? { ...p, isLoading: false } : p
            )
          );
        })
      )
      .subscribe((tramites) => {
        this.panels.update((panels) =>
          panels.map((p) => (p.name === panelName ? { ...p, tramites } : p))
        );
      });
  }
}
