import { Routes } from '@angular/router';
import { Navigation } from './layout/navigation/navigation';
import { authGuard } from './core/auth/guards/auth-guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login-page').then((m) => m.LoginPage),
  },

  {
    path: '',
    loadComponent: () => import('./layout/navigation/navigation').then((m) => m.Navigation),
    canActivate: [authGuard],
    children: [
      {
        path: 'home',
        loadComponent: () => import('./features/home/home-page').then((m) => m.HomePage),
      },
      { path: '', pathMatch: 'full', redirectTo: 'home' },
    ],
  },

  { path: '**', redirectTo: '' },
];
