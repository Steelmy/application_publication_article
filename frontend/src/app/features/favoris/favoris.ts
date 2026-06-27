import { Component, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Header } from '../../shared/components/header/header';
import { ArticleCard } from '../../shared/components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';
import { AuthService } from '../../core/services/auth.service';
import { Article } from '../../models/article.model';

const TAILLE_PAGE = 12;

@Component({
  selector: 'app-favoris',
  imports: [Header, ArticleCard, RouterLink],
  templateUrl: './favoris.html',
})
export class Favoris {
  private readonly articleService = inject(ArticleService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  protected readonly erreur = signal(false);
  protected readonly enChargement = signal(true);

  protected readonly articles = signal<Article[]>([]);
  protected readonly totalElements = signal(0);

  private readonly queryParams = toSignal(this.route.queryParamMap, { initialValue: null });
  protected readonly pageNumero = computed(() => {
    const raw = Number(this.queryParams()?.get('page') ?? 1);
    if (!Number.isFinite(raw) || raw < 1) return 0;
    return Math.floor(raw) - 1;
  });
  protected readonly totalPages = computed(() =>
    Math.max(1, Math.ceil(this.totalElements() / TAILLE_PAGE)),
  );

  protected readonly debutIndex = computed(() =>
    this.articles().length === 0 ? 0 : this.pageNumero() * TAILLE_PAGE + 1,
  );
  protected readonly finIndex = computed(() =>
    this.pageNumero() * TAILLE_PAGE + this.articles().length,
  );

  constructor() {
    effect(() => {
      this.pageNumero();
      untracked(() => this.chargerPage());
    });
  }

  private chargerPage(): void {
    const userId = this.auth.currentUser()?.id;
    if (!userId) return;

    this.enChargement.set(true);
    const pageDemandee = this.pageNumero();
    this.articleService
      .getFavoris(pageDemandee, TAILLE_PAGE, userId)
      .subscribe({
        next: (page) => {
          const totalPages = Math.max(1, Math.ceil(page.totalElements / TAILLE_PAGE));
          if (page.totalElements > 0 && pageDemandee >= totalPages) {
            this.router.navigate([], {
              relativeTo: this.route,
              queryParams: { page: totalPages === 1 ? null : totalPages },
              queryParamsHandling: 'merge',
            });
            return;
          }
          this.articles.set(page.content);
          this.totalElements.set(page.totalElements);
          this.erreur.set(false);
          this.enChargement.set(false);
        },
        error: () => {
          this.erreur.set(true);
          this.enChargement.set(false);
        },
      });
  }

  protected allerPage(n: number): void {
    if (n < 0 || n >= this.totalPages() || n === this.pageNumero()) return;
    this.router
      .navigate([], {
        relativeTo: this.route,
        queryParams: { page: n === 0 ? null : n + 1 },
        queryParamsHandling: 'merge',
      })
      .then(() => window.scrollTo({ top: 0, behavior: 'smooth' }));
  }

  protected retirerArticle(articleId: number): void {
    this.articles.update((list) => list.filter((a) => a.id !== articleId));
    this.totalElements.update((n) => Math.max(0, n - 1));
  }
}
