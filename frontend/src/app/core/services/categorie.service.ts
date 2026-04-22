import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Categorie } from '../../models/categorie.model';

@Injectable({ providedIn: 'root' })
export class CategorieService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/categories`;
  private readonly utilisateursApi = `${environment.apiUrl}/utilisateurs`;

  getAll(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(this.api);
  }

  getAllAvecCompteurs(): Observable<CategorieAvecCompteur[]> {
    return this.http.get<CategorieAvecCompteur[]>(`${this.api}/avec-compteurs`);
  }

  toggleFollow(categorieId: number, utilisateurId: number): Observable<FollowEtat> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http.post<FollowEtat>(`${this.api}/${categorieId}/follow`, null, { params });
  }

  getCategoriesSuivies(utilisateurId: number): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${this.utilisateursApi}/${utilisateurId}/categories-suivies`);
  }
}

export interface FollowEtat {
  nombreAbonnes: number;
  suivieParUtilisateur: boolean;
}

export interface CategorieAvecCompteur {
  id: number;
  nomCategorie: string;
  nbArticles: number;
}
