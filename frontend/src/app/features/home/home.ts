import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { Header } from '../../shared/components/header/header';
import { ArticleCard } from '../../shared/components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';
import { Article } from '../../models/article.model';

@Component({
  selector: 'app-home',
  imports: [Header, ArticleCard, RouterLink],
  templateUrl: './home.html',
})
export class Home {
  private readonly articleService = inject(ArticleService);

  protected readonly erreur = signal(false);

  private readonly articles$ = this.articleService.getAll().pipe(
    catchError(() => {
      this.erreur.set(true);
      return of<Article[]>([]);
    }),
  );

  protected readonly articles = toSignal(this.articles$, { initialValue: null });

  protected readonly enChargement = computed(() => this.articles() === null);

  protected readonly articleUne = computed<Article | null>(() => {
    const liste = this.articles();
    if (!liste || liste.length === 0) return null;
    return [...liste].sort((a, b) => b.nombreDeVues - a.nombreDeVues)[0];
  });

  protected readonly autresArticles = computed<Article[]>(() => {
    const liste = this.articles();
    const une = this.articleUne();
    if (!liste || !une) return [];
    return liste.filter((a) => a.id !== une.id);
  });

  protected readonly categoriesDistinctes = computed(() => {
    const liste = this.articles() ?? [];
    const noms = new Set<string>();
    for (const a of liste) {
      if (a.categorie?.nomCategorie) noms.add(a.categorie.nomCategorie);
    }
    return Array.from(noms);
  });
}
