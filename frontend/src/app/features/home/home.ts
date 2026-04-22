import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { Header } from '../../shared/components/header/header';
import { ArticleCard } from '../../shared/components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';
import { AuthService } from '../../core/services/auth.service';
import { FollowedCategoriesService } from '../../core/services/followed-categories.service';
import { Article } from '../../models/article.model';
import { paletteFor } from '../../shared/constants/category-palettes';

@Component({
  selector: 'app-home',
  imports: [Header, ArticleCard, RouterLink],
  templateUrl: './home.html',
})
export class Home {
  private readonly articleService = inject(ArticleService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly auth = inject(AuthService);
  protected readonly followedCategories = inject(FollowedCategoriesService);
  protected readonly paletteFor = paletteFor;

  protected readonly erreur = signal(false);

  private readonly queryParams = toSignal(this.route.queryParamMap, { initialValue: null });
  protected readonly categorieSelectionnee = computed(() => this.queryParams()?.get('categorie') ?? null);

  private readonly articles$ = this.articleService.getAll().pipe(
    catchError(() => {
      this.erreur.set(true);
      return of<Article[]>([]);
    }),
  );

  protected readonly articles = toSignal(this.articles$, { initialValue: null });

  protected readonly enChargement = computed(() => this.articles() === null);

  private readonly articlesFiltres = computed<Article[]>(() => {
    const liste = this.articles() ?? [];
    const cat = this.categorieSelectionnee();
    return cat ? liste.filter((a) => a.categorie?.nomCategorie === cat) : liste;
  });

  protected readonly articleUne = computed<Article | null>(() => {
    if (this.categorieSelectionnee()) return null;
    const liste = this.articles();
    if (!liste || liste.length === 0) return null;
    return [...liste].sort((a, b) => b.nombreDeVues - a.nombreDeVues)[0];
  });

  protected readonly autresArticles = computed<Article[]>(() => {
    const liste = this.articlesFiltres();
    const une = this.articleUne();
    return une ? liste.filter((a) => a.id !== une.id) : liste;
  });

  protected readonly categoriesDistinctes = computed(() => {
    const liste = this.articles() ?? [];
    const noms = new Set<string>();
    for (const a of liste) {
      if (a.categorie?.nomCategorie) noms.add(a.categorie.nomCategorie);
    }
    return Array.from(noms);
  });

  protected selectionner(nom: string | null): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { categorie: nom },
      queryParamsHandling: 'merge',
    });
  }
}
