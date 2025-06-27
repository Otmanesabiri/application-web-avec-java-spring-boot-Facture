import { Component, OnInit, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ClientService, Client } from '../client.service';
import { NotificationService } from '../services/notification.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Import des modules Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialogModule, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-client-detail',
  templateUrl: './client-detail.component.html',
  styleUrls: ['./client-detail.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule,
    MatDialogModule
  ]
})
export class ClientDetailComponent implements OnInit {
  client: Client | null = null;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clientService: ClientService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadClient(id);
    } else {
      this.notificationService.showError("ID client invalide");
      this.router.navigate(['/clients']);
    }
  }

  private loadClient(id: number): void {
    this.clientService.getClient(id).subscribe({
      next: (data) => {
        this.client = data;
        this.isLoading = false;
      },
      error: (error) => {
        this.notificationService.showError(error.message);
        this.isLoading = false;
        setTimeout(() => this.router.navigate(['/clients']), 2000);
      }
    });
  }

  editClient(): void {
    if (this.client) {
      this.router.navigate(['/clients', this.client.id, 'modifier']);
    }
  }

  deleteClient(): void {
    if (!this.client) return;

    const dialogRef = this.dialog.open(ConfirmDeleteDialog, {
      data: { clientName: this.client.nom }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.client) {
        this.isLoading = true;
        this.clientService.deleteClient(this.client.id).subscribe({
          next: () => {
            this.notificationService.showSuccess(`Client ${this.client!.nom} supprimé avec succès`);
            this.router.navigate(['/clients']);
          },
          error: (error) => {
            this.notificationService.showError(error.message);
            this.isLoading = false;
          }
        });
      }
    });
  }
}

// Composant de dialogue de confirmation
@Component({
  selector: 'confirm-delete-dialog',
  template: `
    <h2 mat-dialog-title>Confirmer la suppression</h2>
    <mat-dialog-content>
      <p>Êtes-vous sûr de vouloir supprimer le client <strong>{{ data.clientName }}</strong> ?</p>
      <p class="warning-text">Cette action est irréversible et supprimera également toutes les factures associées.</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Annuler</button>
      <button mat-raised-button color="warn" [mat-dialog-close]="true">
        <mat-icon>delete</mat-icon>
        Supprimer
      </button>
    </mat-dialog-actions>
  `,
  styles: [`
    .warning-text {
      color: #f44336;
      font-size: 0.9em;
      margin-top: 8px;
    }
  `],
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatIconModule]
})
export class ConfirmDeleteDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { clientName: string }) {}
}
