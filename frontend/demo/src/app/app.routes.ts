import { Routes } from '@angular/router';
import { Navigation } from './layout/navigation/navigation';
import { authGuard, adminGuard } from './core/auth/guards/auth.guard';
import {RadicarTramiteComponent} from './features/tramites/radicar-tramite.component';
import {LoginComponent} from './features/auth/login.component';

export const routes: Routes = [
  {
    path: '',
    component: Navigation,
    canActivate: [authGuard],
    children: [
      {
        path: 'tramites/radicar',
        loadComponent: () =>
          import('./features/tramites/radicar-tramite.component')
            .then((m) => m.RadicarTramiteComponent),
      },
      {
        path: 'admin/tramites',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./features/admin/tramites/tramites-list.component')
            .then((m) => m.TramitesListComponent),
      },
      { path: '', redirectTo: 'tramites/radicar', pathMatch: 'full' },
    ],
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login.component')
        .then((m) => m.LoginComponent),
  },
  { path: '**', redirectTo: '' },
];
