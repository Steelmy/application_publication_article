import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Article } from '../../models/article.model';

@Injectable({ providedIn: 'root' })
export class ArticleService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/articles`;

  getAll(): Observable<Article[]> {
    return this.http.get<Article[]>(this.api);
  }

  getById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.api}/${id}`);
  }

  toggleLike(articleId: number, utilisateurId: number): Observable<LikeEtat> {
    const params = new HttpParams().set('utilisateurId', utilisateurId);
    return this.http.post<LikeEtat>(`${this.api}/${articleId}/like`, null, { params });
  }
}

export interface LikeEtat {
  nombreLikes: number;
  likeParUtilisateur: boolean;
}
