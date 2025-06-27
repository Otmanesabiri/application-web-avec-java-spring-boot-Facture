import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { Client } from './client.service';

export interface LigneFacture {
  id?: number;
  description: string;
  quantite: number;
  prixUnitaireHT: number;
  tauxTVA: number;
  montantHT?: number;
}

export interface Facture {
  id: number;
  numero?: string;
  date: string;
  clientId: number;
  client?: Client;
  lignes: LigneFacture[];
  totalHT?: number;
  totalTVA?: number;
  totalTTC?: number;
  montantTotal?: number; // Garde pour compatibilité
}

@Injectable({ providedIn: 'root' })
export class FactureService {
  private apiUrl = 'http://localhost:8095/api/factures';

  constructor(private http: HttpClient) {}

  getFactures(): Observable<Facture[]> {
    return this.http.get<Facture[]>(this.apiUrl).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getFacture(id: number): Observable<Facture> {
    return this.http.get<Facture>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  createFacture(facture: Partial<Facture>): Observable<Facture> {
    return this.http.post<Facture>(this.apiUrl, facture).pipe(
      catchError(this.handleError)
    );
  }

  updateFacture(id: number, facture: Partial<Facture>): Observable<Facture> {
    return this.http.put<Facture>(`${this.apiUrl}/${id}`, facture).pipe(
      catchError(this.handleError)
    );
  }

  deleteFacture(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  getFacturesByClient(clientId: number): Observable<Facture[]> {
    return this.http.get<Facture[]>(`${this.apiUrl}/recherche/client/${clientId}`).pipe(
      catchError(this.handleError)
    );
  }

  exportFactureJSON(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${id}/export`, { responseType: 'text' }).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Une erreur inattendue s\'est produite';

    if (error.error instanceof ErrorEvent) {
      // Erreur côté client
      errorMessage = `Erreur: ${error.error.message}`;
    } else {
      // Erreur côté serveur
      switch (error.status) {
        case 400:
          errorMessage = error.error?.message || 'Données de facture invalides';
          break;
        case 404:
          errorMessage = 'Facture non trouvée';
          break;
        case 422:
          errorMessage = 'Erreur de validation des données';
          break;
        case 500:
          errorMessage = 'Erreur serveur. Veuillez réessayer plus tard';
          break;
        default:
          errorMessage = `Erreur ${error.status}: ${error.message}`;
      }
    }

    return throwError(() => new Error(errorMessage));
  }
}
