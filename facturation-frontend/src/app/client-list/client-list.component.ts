import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { ClientService, Client } from '../client.service';
import { Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { filter } from 'rxjs/operators';

// Import des modules Angular Material
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { MatSortModule, Sort, MatSort } from '@angular/material/sort';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule
  ]
})
export class ClientListComponent implements OnInit, AfterViewInit {
  // Tableau pour stocker les clients
  clients: Client[] = [];
  // Source de données pour le tableau Material
  dataSource = new MatTableDataSource<Client>([]);
  // Colonnes à afficher dans le tableau
  displayedColumns: string[] = ['id', 'nom', 'siret', 'email', 'dateCreation', 'actions'];

  isLoading = true;
  error: string | null = null;

  // Références aux composants enfants pour le tri et la pagination
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private clientService: ClientService, private router: Router) {
    // S'abonner aux événements de navigation pour recharger les données
    // à chaque fois que l'utilisateur navigue vers cette page
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd && event.url === '/clients')
    ).subscribe(() => {
      this.loadClients();
    });
  }

  ngOnInit(): void {
    this.loadClients();
  }

  ngAfterViewInit(): void {
    // Associer le tri et la pagination à la source de données après l'initialisation de la vue
    if (this.dataSource) {
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }
  }

  loadClients(): void {
    this.isLoading = true;
    this.error = null;

    console.log('Chargement des clients...');

    this.clientService.getClients().subscribe({
      next: (data) => {
        console.log('Clients reçus:', data);
        this.clients = data;
        this.dataSource.data = data; // Mise à jour de la source de données du tableau
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des clients:', err);
        this.error = 'Erreur lors du chargement des clients';
        this.isLoading = false;
      }
    });
  }

  goToDetail(client: Client) {
    this.router.navigate(['/clients', client.id]);
  }

  // Méthode pour forcer le rechargement des données
  refreshClients(): void {
    this.loadClients();
  }
}
