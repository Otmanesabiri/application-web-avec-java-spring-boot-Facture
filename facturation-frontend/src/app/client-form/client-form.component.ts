import { Component, OnInit } from '@angular/core';
import { ClientService, Client } from '../client.service';
import { NotificationService } from '../services/notification.service';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Angular Material imports
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-client-form',
  templateUrl: './client-form.component.html',
  styleUrls: ['./client-form.component.scss'],
  standalone: true,
  imports: [
    FormsModule,
    CommonModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressBarModule,
    MatCardModule
  ]
})
export class ClientFormComponent implements OnInit {
  client: Partial<Client> = {};
  isSaving = false;
  isEditing = false;

  constructor(
    private clientService: ClientService,
    private notificationService: NotificationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditing = true;
      this.loadClient(Number(id));
    }
  }

  loadClient(id: number): void {
    this.clientService.getClient(id).subscribe({
      next: (client) => {
        this.client = client;
      },
      error: (error) => {
        this.notificationService.showError(error.message);
        this.router.navigate(['/clients']);
      }
    });
  }

  saveClient() {
    if (!this.validateForm()) {
      return;
    }

    this.isSaving = true;

    const operation = this.isEditing
      ? this.clientService.updateClient(this.client.id!, this.client)
      : this.clientService.createClient(this.client);

    operation.subscribe({
      next: () => {
        this.isSaving = false;
        const message = this.isEditing
          ? 'Client modifié avec succès'
          : 'Client créé avec succès';
        this.notificationService.showSuccess(message);
        this.router.navigate(['/clients']);
      },
      error: (error) => {
        this.isSaving = false;
        this.notificationService.showError(error.message);
      }
    });
  }

  private validateForm(): boolean {
    if (!this.client.nom?.trim()) {
      this.notificationService.showWarning('Le nom est obligatoire');
      return false;
    }
    if (!this.client.email?.trim()) {
      this.notificationService.showWarning('L\'email est obligatoire');
      return false;
    }
    if (!this.client.siret?.trim()) {
      this.notificationService.showWarning('Le SIRET est obligatoire');
      return false;
    }
    if (!/^\d{14}$/.test(this.client.siret)) {
      this.notificationService.showWarning('Le SIRET doit contenir exactement 14 chiffres');
      return false;
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.client.email)) {
      this.notificationService.showWarning('Format d\'email invalide');
      return false;
    }
    return true;
  }

  cancel(): void {
    this.router.navigate(['/clients']);
  }
}
