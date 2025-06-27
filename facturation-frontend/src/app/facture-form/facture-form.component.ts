import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FactureService, Facture, LigneFacture } from '../facture.service';
import { ClientService, Client } from '../client.service';
import { NotificationService } from '../services/notification.service';

// Angular Material imports
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-facture-form',
  templateUrl: './facture-form.component.html',
  styleUrls: ['./facture-form.component.scss'],
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatCardModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatTableModule
  ]
})
export class FactureFormComponent implements OnInit {
  facture: Partial<Facture> = {
    date: new Date().toISOString().split('T')[0],
    lignes: []
  };

  clients: Client[] = [];
  isEditing = false;
  isSaving = false;
  isLoading = true;
  displayedColumns: string[] = ['description', 'quantite', 'prixUnitaireHT', 'tauxTVA', 'montantHT', 'actions'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private factureService: FactureService,
    private clientService: ClientService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    // Chargement des clients pour le sélecteur
    this.clientService.getClients().subscribe({
      next: (clients) => {
        this.clients = clients;

        // Vérifier s'il s'agit d'une édition (id présent dans l'URL)
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
          this.isEditing = true;
          this.loadFacture(id);
        } else {
          this.isLoading = false;
          this.addLigne(); // Ajouter une ligne par défaut
        }
      },
      error: (error) => {
        this.notificationService.showError(error.message);
        this.isLoading = false;
      }
    });
  }

  loadFacture(id: number): void {
    this.factureService.getFacture(id).subscribe({
      next: (data) => {
        this.facture = { ...data };
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError(error.message);
        this.isLoading = false;
        this.router.navigate(['/factures']);
      }
    });
  }

  addLigne(): void {
    if (!this.facture.lignes) {
      this.facture.lignes = [];
    }

    this.facture.lignes.push({
      description: '',
      quantite: 1,
      prixUnitaireHT: 0,
      tauxTVA: 20,
      montantHT: 0
    });
  }

  removeLigne(index: number): void {
    if (this.facture.lignes && this.facture.lignes.length > 1) {
      this.facture.lignes.splice(index, 1);
      this.calculerTotaux();
    }
  }

  calculerMontantLigne(ligne: LigneFacture): void {
    ligne.montantHT = ligne.quantite * ligne.prixUnitaireHT;
    this.calculerTotaux();
  }

  calculerTotaux(): void {
    if (this.facture.lignes && this.facture.lignes.length > 0) {
      this.facture.totalHT = this.facture.lignes.reduce(
        (total, ligne) => total + (ligne.montantHT || 0),
        0
      );

      this.facture.totalTVA = this.facture.lignes.reduce(
        (total, ligne) => {
          const montantHT = ligne.quantite * ligne.prixUnitaireHT;
          const montantTVA = (montantHT * ligne.tauxTVA) / 100;
          return total + montantTVA;
        },
        0
      );

      this.facture.totalTTC = this.facture.totalHT + this.facture.totalTVA;
      this.facture.montantTotal = this.facture.totalTTC; // Pour compatibilité
    } else {
      this.facture.totalHT = 0;
      this.facture.totalTVA = 0;
      this.facture.totalTTC = 0;
      this.facture.montantTotal = 0;
    }
  }

  saveFacture(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isSaving = true;
    this.calculerTotaux();

    // Assurer qu'il y a au moins une ligne
    if (!this.facture.lignes || this.facture.lignes.length === 0) {
      this.addLigne();
    }

    const operation = this.isEditing && this.facture.id
      ? this.factureService.updateFacture(this.facture.id, this.facture)
      : this.factureService.createFacture(this.facture);

    operation.subscribe({
      next: () => {
        this.isSaving = false;
        const message = this.isEditing
          ? 'Facture modifiée avec succès'
          : 'Facture créée avec succès';
        this.notificationService.showSuccess(message);
        this.router.navigate(['/factures']);
      },
      error: (error) => {
        this.isSaving = false;
        this.notificationService.showError(error.message);
      }
    });
  }

  private validateForm(): boolean {
    if (!this.facture.clientId) {
      this.notificationService.showWarning('Veuillez sélectionner un client');
      return false;
    }

    if (!this.facture.date) {
      this.notificationService.showWarning('La date est obligatoire');
      return false;
    }

    if (!this.facture.lignes || this.facture.lignes.length === 0) {
      this.notificationService.showWarning('Au moins une ligne de facturation est requise');
      return false;
    }

    // Valider chaque ligne
    for (let i = 0; i < this.facture.lignes.length; i++) {
      const ligne = this.facture.lignes[i];
      if (!ligne.description?.trim()) {
        this.notificationService.showWarning(`Description requise pour la ligne ${i + 1}`);
        return false;
      }
      if (ligne.quantite <= 0) {
        this.notificationService.showWarning(`Quantité invalide pour la ligne ${i + 1}`);
        return false;
      }
      if (ligne.prixUnitaireHT < 0) {
        this.notificationService.showWarning(`Prix unitaire invalide pour la ligne ${i + 1}`);
        return false;
      }
    }

    return true;
  }

  cancel(): void {
    this.router.navigate(['/factures']);
  }

  getClientName(clientId: number): string {
    const client = this.clients.find(c => c.id === clientId);
    return client ? client.nom : `Client #${clientId}`;
  }
}
