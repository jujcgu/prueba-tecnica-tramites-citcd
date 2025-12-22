import {ChangeDetectionStrategy, Component, effect, inject, signal,} from '@angular/core';
import {toSignal} from '@angular/core/rxjs-interop';
import {catchError, EMPTY, finalize, map, of,} from 'rxjs';
import {TramitesAdminService} from './data-access/tramites-admin.service';
import {TramiteDetalle} from './models/tramite-admin.model';
import {
  MatAccordion,
  MatExpansionPanel, MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from '@angular/material/expansion';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatIcon} from '@angular/material/icon';
import {DatePipe, TitleCasePipe} from '@angular/common';
import {MatTable} from '@angular/material/table';

// ViewModel for each status panel
interface StatusPanel {
  name: string;
  tramites: TramiteDetalle[];
  isLoading: boolean;
  error: string | null;
}

@Component({
  selector: 'app-tramites-list',
  templateUrl: './tramites-list.component.html',
  styleUrls: ['./tramites-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatExpansionPanelTitle,
    MatExpansionPanelHeader,
    MatExpansionPanel,
    MatAccordion,
    MatExpansionPanelDescription,
    MatProgressSpinner,
    MatIcon,
    DatePipe,
    MatTable,
    TitleCasePipe
  ]
})
export class TramitesListComponent {
  // Signal to hold the state of all status panels
  readonly panels = signal<StatusPanel[]>([]);

  // --- State Signals ---
  // Signal to track which panel is currently open
  readonly expandedPanel = signal<string | null>(null);
  private readonly tramitesAdminApi = inject(TramitesAdminService);

  // --- Data Signals from APIs ---
  // Fetch all available statuses and initialize the panels
  private readonly estados$ = this.tramitesAdminApi.getEstados().pipe(map((statuses) => statuses.map((s) => ({
    name: s, tramites: [], isLoading: false, // Initially, no data is loading
    error: null,
  } as StatusPanel))), catchError(() => {
    // On error, create a single panel with an error message
    return of([{
      name: 'Error', tramites: [], isLoading: false, error: 'No se pudieron cargar los estados de los trámites.',
    },] as StatusPanel[]);
  }));
  readonly statuses = toSignal(this.estados$, {initialValue: []});

  constructor() {
    // Initialize the panels with the fetched statuses
    this.panels.set(this.statuses());

    // --- Reactive Effect ---
    // This effect runs whenever the expandedPanel or statuses signal changes.
    effect(() => {
      const panelName = this.expandedPanel();
      const allPanels = this.panels();

      if (!panelName) return; // If no panel is open, do nothing.

      const panel = allPanels.find((p) => p.name === panelName);

      // If panel is found, has no tramites, is not loading, and has no error, fetch data.
      if (panel && panel.tramites.length === 0 && !panel.isLoading && !panel.error) {
        this.fetchTramitesForPanel(panelName);
      }
    }, {allowSignalWrites: true});
  }

  /**
   * Toggles a panel's expansion state and triggers data fetching.
   * @param panelName The name of the panel to toggle.
   */
  togglePanel(panelName: string): void {
    if (this.expandedPanel() === panelName) {
      this.expandedPanel.set(null); // Close if already open
    } else {
      this.expandedPanel.set(panelName); // Open and trigger effect
    }
  }

  private fetchTramitesForPanel(panelName: string): void {
    // Set loading state to true for the specific panel
    this.panels.update((panels) => panels.map((p) => p.name === panelName ? {...p, isLoading: true, error: null} : p));

    this.tramitesAdminApi
      .getTramitesPorEstado(panelName)
      .pipe(catchError((err) => {
        console.error(err);
        // On error, update the panel with an error message
        this.panels.update((panels) => panels.map((p) => p.name === panelName ? {
          ...p, error: 'Error al cargar los trámites. Intente de nuevo.',
        } : p));
        return EMPTY;
      }), finalize(() => {
        // Ensure loading is set to false after completion or error
        this.panels.update((panels) => panels.map((p) => p.name === panelName ? {...p, isLoading: false} : p));
      }))
      .subscribe((tramites) => {
        // On success, update the panel with the fetched data
        this.panels.update((panels) => panels.map((p) => p.name === panelName ? {...p, tramites} : p));
      });
  }
}
