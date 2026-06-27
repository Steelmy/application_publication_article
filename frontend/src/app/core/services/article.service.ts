import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Article, Page } from '../../models/article.model';

export interface PageRequete {
  page: number;
  size: number;
  categorie?: string | null;
  utilisateurId?: number;
}

@Injectable({ providedIn: 'root' })
export class ArticleService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/articles`;

  getAll(req: PageRequete): Observable<Page<Article>> {
    let params = new HttpParams().set('page', req.page).set('size', req.size);
    if (req.categorie) params = params.set('categorie', req.categorie);
    if (req.utilisateurId) params = params.set('utilisateurId', req.utilisateurId);
    return this.http.get<Page<Article>>(this.api, { params });
  }

  getUne(utilisateurId?: number): Observable<Article> {
    const options = utilisateurId ? { params: new HttpParams().set('utilisateurId', utilisateurId) } : {};
    return this.http.get<Article>(`${this.api}/une`, options);
  }

  getSimilaires(articleId: number, limit = 3, utilisateurId?: number): Observable<Article[]> {
    let params = new HttpParams().set('limit', limit);
    if (utilisateurId) params = params.set('utilisateurId', utilisateurId);
    return this.http.get<Article[]>(`${this.api}/${articleId}/similaires`, { params });
  }

  getById(id: number, utilisateurId?: number): Observable<Article> {
    const options = utilisateurId ? { params: new HttpParams().set('utilisateurId', utilisateurId) } : {};
    return this.http.get<Article>(`${this.api}/${id}`, options);
  }

  toggleLike(articleId: number, utilisateurId: number): Observable<LikeEtat> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http.post<LikeEtat>(`${this.api}/${articleId}/like`, null, { params });
  }

  getFavoris(page: number, size: number, utilisateurId: number): Observable<Page<Article>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('utilisateurId', utilisateurId);
    return this.http.get<Page<Article>>(`${this.api}/favoris`, { params });
  }
}

export interface LikeEtat {
  nombreLikes: number;
  likeParUtilisateur: boolean;
}
