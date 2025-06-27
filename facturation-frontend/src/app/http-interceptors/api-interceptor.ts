import { Injectable } from '@angular/core';
import {
  HttpEvent, HttpInterceptor, HttpHandler,
  HttpRequest, HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry, timeout } from 'rxjs/operators';

@Injectable()
export class ApiInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Définir les en-têtes communs
    const apiReq = req.clone({
      headers: req.headers
        .set('Content-Type', 'application/json')
        .set('Accept', 'application/json')
    });

    // Ajouter un timeout et des retries pour améliorer la fiabilité
    return next.handle(apiReq).pipe(
      timeout(30000),  // 30 secondes de timeout
      retry(2),        // 2 tentatives en cas d'échec
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = '';

    if (error.status === 0) {
      // Erreur côté client ou problème réseau
      console.error('Une erreur réseau s\'est produite:', error.error);
      errorMessage = 'Erreur de connexion au serveur. Veuillez vérifier votre connexion réseau.';
    } else {
      // Le backend a renvoyé un code d'erreur
      console.error(
        `Le backend a retourné le code ${error.status}, ` +
        `le corps était: ${JSON.stringify(error.error)}`);

      // Personnalisation des messages d'erreur selon le code HTTP
      switch (error.status) {
        case 400:
          errorMessage = 'Requête invalide. Veuillez vérifier vos données.';
          break;
        case 401:
          errorMessage = 'Non autorisé. Veuillez vous reconnecter.';
          break;
        case 403:
          errorMessage = 'Accès refusé. Vous n\'avez pas les droits nécessaires.';
          break;
        case 404:
          errorMessage = 'Ressource introuvable.';
          break;
        case 500:
          errorMessage = 'Erreur serveur. Veuillez réessayer plus tard.';
          break;
        default:
          errorMessage = `Erreur: ${error.message}`;
      }
    }

    // Retourner une erreur observable avec un message convivial
    return throwError(() => new Error(errorMessage));
  }
}
