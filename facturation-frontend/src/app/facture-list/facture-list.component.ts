import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FactureService, Facture } from '../facture.service';
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
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-facture-list',
  templateUrl: './facture-list.component.html',
  styleUrls: ['./facture-list.component.scss'],
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
    MatTooltipModule,
    MatInputModule,
    MatFormFieldModule,
    MatCardModule
  ]
})
export class FactureListComponent implements OnInit, AfterViewInit {
  // Tableau pour stocker les factures
  factures: Facture[] = [];
  // Source de données pour le tableau Material
  dataSource = new MatTableDataSource<Facture>([]);
  // Colonnes à afficher dans le tableau
  displayedColumns: string[] = ['numero', 'date', 'client', 'montantTotal', 'actions'];

  isLoading = true;
  error: string | null = null;

  // Références aux composants enfants pour le tri et la pagination
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private factureService: FactureService, private router: Router) {
    // S'abonner aux événements de navigation pour recharger les données
    // à chaque fois que l'utilisateur navigue vers cette page
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd && event.url === '/factures')
    ).subscribe(() => {
      this.loadFactures();
    });
  }

  ngOnInit(): void {
    this.loadFactures();
  }

  ngAfterViewInit(): void {
    // Associer le tri et la pagination à la source de données après l'initialisation de la vue
    if (this.dataSource) {
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }
  }

  loadFactures(): void {
    this.isLoading = true;
    this.error = null;

    console.log('Chargement des factures...');

    this.factureService.getFactures().subscribe({
      next: (data) => {
        console.log('Factures reçues:', data);
        this.factures = data;
        this.dataSource.data = data; // Mise à jour de la source de données du tableau
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des factures:', err);
        this.error = 'Erreur lors du chargement des factures';
        this.isLoading = false;
      }
    });
  }

  goToDetail(facture: Facture) {
    this.router.navigate(['/factures', facture.id]);
  }

  deleteFacture(facture: Facture): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer la facture ${facture.numero} ?`)) {
      this.isLoading = true;
      this.factureService.deleteFacture(facture.id).subscribe({
        next: () => {
          // Recharger la liste après suppression
          this.loadFactures();
        },
        error: (err) => {
          console.error('Erreur lors de la suppression de la facture:', err);
          this.error = 'Erreur lors de la suppression de la facture';
          this.isLoading = false;
        }
      });
    }
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }
}
