import { Routes } from '@angular/router';
import { Navigation } from './layout/navigation/navigation';
import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then((m) => m.LoginComponent),
  },
  {
    path: '',
    loadComponent: () => import('./layout/navigation/navigation').then((m) => m.Navigation),
    canActivate: [authGuard],
    children: [
      {
        path: 'tramites/radicar',
        loadComponent: () =>
          import('./features/tramites/radicar-tramite.component').then((m) => m.RadicarTramitePageComponent),
      },
      { path: '', pathMatch: 'full', redirectTo: 'tramites/radicar' },
    ],
  },

  { path: '**', redirectTo: '' },
];
