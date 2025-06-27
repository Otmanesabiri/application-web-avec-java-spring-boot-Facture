import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface NotificationMessage {
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  duration?: number;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private notificationSubject = new BehaviorSubject<NotificationMessage | null>(null);
  public notification$ = this.notificationSubject.asObservable();

  showSuccess(message: string, duration: number = 5000) {
    this.showNotification({ type: 'success', message, duration });
  }

  showError(message: string, duration: number = 8000) {
    this.showNotification({ type: 'error', message, duration });
  }

  showWarning(message: string, duration: number = 6000) {
    this.showNotification({ type: 'warning', message, duration });
  }

  showInfo(message: string, duration: number = 5000) {
    this.showNotification({ type: 'info', message, duration });
  }

  private showNotification(notification: NotificationMessage) {
    this.notificationSubject.next(notification);

    if (notification.duration && notification.duration > 0) {
      setTimeout(() => {
        this.clearNotification();
      }, notification.duration);
    }
  }

  clearNotification() {
    this.notificationSubject.next(null);
  }
}
