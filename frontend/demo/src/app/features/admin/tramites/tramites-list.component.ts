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

// ViewModel for each status panel
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
  // This signal gets the initial panel structure from the API.
  private readonly initialPanels = toSignal(
    this.tramitesAdminApi.getEstados().pipe(
      map((statuses) =>
        statuses.map(
          (s) =>
            ({
              name: s,
              tramites: [],
              isLoading: false,
              error: null,
            } as StatusPanel)
        )
      ),
      catchError(() => {
        return of([
          {
            name: 'Error',
            tramites: [],
            isLoading: false,
            error: 'No se pudieron cargar los estados de los trámites.',
          },
        ] as StatusPanel[]);
      })
    ),
    { initialValue: [] }
  );

  constructor() {
    // --- Reactive Effects ---

    // Effect 1: Initializes the panels signal when the API data arrives.
    // This fixes the race condition.
    effect(() => {
      const panels = this.initialPanels();
      this.panels.set(panels);
    });

    // Effect 2: Fetches data for a panel when it's expanded.
    effect(
      () => {
        const panelName = this.expandedPanel();
        const allPanels = this.panels();

        if (!panelName) return; // Exit if no panel is expanded

        const panel = allPanels.find((p) => p.name === panelName);

        // Fetch data only if the panel exists, is empty, and not already loading.
        if (panel && panel.tramites.length === 0 && !panel.isLoading && !panel.error) {
          this.fetchTramitesForPanel(panelName);
        }
      },
      { allowSignalWrites: true } // Needed because fetchTramitesForPanel updates a signal
    );
  }

  /**
   * Toggles a panel's expansion state.
   * @param panelName The name of the panel to toggle.
   */
  togglePanel(panelName: string): void {
    if (this.expandedPanel() === panelName) {
      this.expandedPanel.set(null); // Close if already open
    } else {
      this.expandedPanel.set(panelName); // Open and trigger effect
    }
  }

  /**
   * Fetches and loads the tramites for a specific panel.
   * @param panelName The name of the panel to fetch data for.
   */
  private fetchTramitesForPanel(panelName: string): void {
    this.panels.update((panels) =>
      panels.map((p) =>
        p.name === panelName ? { ...p, isLoading: true, error: null } : p
      )
    );

    this.tramitesAdminApi
      .getTramitesPorEstado(panelName)
      .pipe(
        catchError((err) => {
          console.error(err);
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
          panels.map((p) =>
            p.name === panelName ? { ...p, tramites } : p
          )
        );
      });
  }
}
