import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      // Routes pour les clients
      { path: 'clients', loadComponent: () => import('./client-list/client-list.component').then(m => m.ClientListComponent) },
      { path: 'clients/nouveau', loadComponent: () => import('./client-form/client-form.component').then(m => m.ClientFormComponent) },
      { path: 'clients/:id', loadComponent: () => import('./client-detail/client-detail.component').then(m => m.ClientDetailComponent) },

      // Routes pour les factures
      { path: 'factures', loadComponent: () => import('./facture-list/facture-list.component').then(m => m.FactureListComponent) },
      { path: 'factures/nouvelle', loadComponent: () => import('./facture-form/facture-form.component').then(m => m.FactureFormComponent) },
      { path: 'factures/:id', loadComponent: () => import('./facture-detail/facture-detail.component').then(m => m.FactureDetailComponent) },
      { path: 'factures/:id/modifier', loadComponent: () => import('./facture-form/facture-form.component').then(m => m.FactureFormComponent) },

      // Route par défaut
      { path: '', redirectTo: 'clients', pathMatch: 'full' },
    ]
  },
  // Redirection globale
  { path: '**', redirectTo: '' }
];
