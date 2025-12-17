import { Routes } from '@angular/router';
import { Navigation } from './layout/navigation/navigation';
import { HomePage } from './features/home/home-page';

export const routes: Routes = [
  {
    path: '',
    component: Navigation,
    children: [
      {
        path: 'home',
        loadComponent: () => import('./features/home/home-page').then((m) => m.HomePage),
      },
    ],
  },
];
