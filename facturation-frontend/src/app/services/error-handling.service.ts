import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ErrorHandlingService {

  constructor() { }

  /**
   * Traite les erreurs HTTP et retourne un message d'erreur convivial
   * @param error L'erreur HTTP reçue
   * @returns Observable d'erreur avec un message adapté
   */
  handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = '';

    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Erreur côté serveur
      switch (error.status) {
        case 0:
          errorMessage = 'Erreur de connexion au serveur. Vérifiez votre connexion internet.';
          break;
        case 400:
          errorMessage = this.parseValidationErrors(error.error) ||
                        'Données invalides. Veuillez vérifier votre saisie.';
          break;
        case 401:
          errorMessage = 'Session expirée. Veuillez vous reconnecter.';
          break;
        case 403:
          errorMessage = 'Vous n\'avez pas les permissions nécessaires pour cette action.';
          break;
        case 404:
          errorMessage = 'La ressource demandée n\'existe pas.';
          break;
        case 409:
          errorMessage = 'Cette opération crée un conflit avec les données existantes.';
          break;
        case 500:
          errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.message}`;
      }
    }

    console.error('Erreur API:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Parse les erreurs de validation du backend Spring
   */
  private parseValidationErrors(errorBody: any): string | null {
    if (!errorBody || !errorBody.errors) {
      return null;
    }

    try {
      const messages = errorBody.errors.map((err: any) =>
        `${err.field}: ${err.message}`
      );
      return messages.join(', ');
    } catch (e) {
      return null;
    }
  }
}
