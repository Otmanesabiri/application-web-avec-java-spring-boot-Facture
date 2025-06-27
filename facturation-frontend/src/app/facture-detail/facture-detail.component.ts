import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FactureService, Facture } from '../facture.service';
import { NotificationService } from '../services/notification.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatTableModule } from '@angular/material/table';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-facture-detail',
  templateUrl: './facture-detail.component.html',
  styleUrls: ['./facture-detail.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatTableModule,
    MatDialogModule
  ]
})
export class FactureDetailComponent implements OnInit {
  facture: Facture | null = null;
  isLoading = true;
  displayedColumns: string[] = ['description', 'quantite', 'prixUnitaireHT', 'tauxTVA', 'montantHT'];

  constructor(
    private route: ActivatedRoute,
    private factureService: FactureService,
    private notificationService: NotificationService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadFacture(id);
    } else {
      this.notificationService.showError("ID facture invalide");
      this.router.navigate(['/factures']);
    }
  }

  private loadFacture(id: number): void {
    this.factureService.getFacture(id).subscribe({
      next: (data) => {
        this.facture = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError(error.message);
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/factures']), 2000);
      }
    });
  }

  deleteFacture(): void {
    if (!this.facture) return;

    if (confirm(`Êtes-vous sûr de vouloir supprimer cette facture ?`)) {
      this.isLoading = true;
      this.factureService.deleteFacture(this.facture.id).subscribe({
        next: () => {
          this.notificationService.showSuccess('Facture supprimée avec succès');
          this.router.navigate(['/factures']);
        },
        error: (error) => {
          this.notificationService.showError(error.message);
          this.isLoading = false;
        }
      });
    }
  }

  editFacture(): void {
    if (this.facture) {
      this.router.navigate(['/factures', this.facture.id, 'modifier']);
    }
  }

  exportFacture(): void {
    if (!this.facture) return;

    this.factureService.exportFactureJSON(this.facture.id).subscribe({
      next: (jsonData) => {
        this.downloadJSON(jsonData, `facture-${this.facture!.id}.json`);
        this.notificationService.showSuccess('Facture exportée avec succès');
      },
      error: (error) => {
        this.notificationService.showError(error.message);
      }
    });
  }

  private downloadJSON(data: string, filename: string): void {
    const blob = new Blob([data], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  calculateLineTotal(ligne: any): number {
    return ligne.quantite * ligne.prixUnitaireHT;
  }
}
