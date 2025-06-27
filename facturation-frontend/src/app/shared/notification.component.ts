import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { NotificationService, NotificationMessage } from '../services/notification.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  template: `
    <div *ngIf="notification"
         class="notification-container"
         [ngClass]="'notification-' + notification.type">
      <div class="notification-content">
        <mat-icon class="notification-icon">
          {{ getIcon(notification.type) }}
        </mat-icon>
        <span class="notification-message">{{ notification.message }}</span>
        <button mat-icon-button
                class="notification-close"
                (click)="closeNotification()">
          <mat-icon>close</mat-icon>
        </button>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 1000;
      min-width: 300px;
      max-width: 500px;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      animation: slideIn 0.3s ease-out;
    }

    .notification-content {
      display: flex;
      align-items: center;
      padding: 16px;
      color: white;
    }

    .notification-icon {
      margin-right: 12px;
      font-size: 24px;
      width: 24px;
      height: 24px;
    }

    .notification-message {
      flex: 1;
      font-size: 14px;
      line-height: 1.4;
    }

    .notification-close {
      margin-left: 8px;
      color: inherit;
    }

    .notification-success {
      background-color: #4caf50;
    }

    .notification-error {
      background-color: #f44336;
    }

    .notification-warning {
      background-color: #ff9800;
    }

    .notification-info {
      background-color: #2196f3;
    }

    @keyframes slideIn {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }
  `]
})
export class NotificationComponent implements OnInit, OnDestroy {
  notification: NotificationMessage | null = null;
  private subscription?: Subscription;

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.subscription = this.notificationService.notification$.subscribe(
      notification => this.notification = notification
    );
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
  }

  getIcon(type: string): string {
    switch (type) {
      case 'success': return 'check_circle';
      case 'error': return 'error';
      case 'warning': return 'warning';
      case 'info': return 'info';
      default: return 'info';
    }
  }

  closeNotification() {
    this.notificationService.clearNotification();
  }
}
