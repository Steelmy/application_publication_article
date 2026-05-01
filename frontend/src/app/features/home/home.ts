import { Component, OnInit, computed, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Header } from '../../shared/components/header/header';
import { ArticleCard } from '../../shared/components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';
import { CategorieService } from '../../core/services/categorie.service';
import { AuthService } from '../../core/services/auth.service';
import { FollowedCategoriesService } from '../../core/services/followed-categories.service';
import { Article } from '../../models/article.model';
import { Categorie } from '../../models/categorie.model';
import { paletteFor } from '../../shared/constants/category-palettes';

const TAILLE_PAGE = 12;

@Component({
  selector: 'app-home',
  imports: [Header, ArticleCard, RouterLink],
  templateUrl: './home.html',
})
export class Home implements OnInit {
  private readonly articleService = inject(ArticleService);
  private readonly categorieService = inject(CategorieService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly auth = inject(AuthService);
  protected readonly followedCategories = inject(FollowedCategoriesService);
  protected readonly paletteFor = paletteFor;

  protected readonly erreur = signal(false);
  protected readonly enChargement = signal(true);

  protected readonly articleUne = signal<Article | null>(null);
  protected readonly articles = signal<Article[]>([]);
  protected readonly totalElements = signal(0);
  private readonly toutesCategories = signal<Categorie[]>([]);

  private readonly queryParams = toSignal(this.route.queryParamMap, { initialValue: null });
  protected readonly categorieSelectionnee = computed(() => this.queryParams()?.get('categorie') ?? null);
  protected readonly pageNumero = computed(() => {
    const raw = Number(this.queryParams()?.get('page') ?? 1);
    if (!Number.isFinite(raw) || raw < 1) return 0;
    return Math.floor(raw) - 1;
  });
  protected readonly totalPages = computed(() =>
    Math.max(1, Math.ceil(this.totalElements() / TAILLE_PAGE)),
  );

  protected readonly categoriesDistinctes = computed(() =>
    this.toutesCategories().map((c) => c.nomCategorie),
  );

  protected readonly debutIndex = computed(() =>
    this.articles().length === 0 ? 0 : this.pageNumero() * TAILLE_PAGE + 1,
  );
  protected readonly finIndex = computed(() =>
    this.pageNumero() * TAILLE_PAGE + this.articles().length,
  );

  constructor() {
    effect(() => {
      this.categorieSelectionnee();
      this.pageNumero();
      untracked(() => this.chargerPage());
    });
  }

  ngOnInit(): void {
    this.categorieService.getAll().subscribe({
      next: (cats) => this.toutesCategories.set(cats),
      error: () => this.toutesCategories.set([]),
    });
    this.articleService.getUne(this.auth.currentUser()?.id).subscribe({
      next: (une) => this.articleUne.set(une),
      error: () => this.articleUne.set(null),
    });
  }

  private chargerPage(): void {
    this.enChargement.set(true);
    const pageDemandee = this.pageNumero();
    this.articleService
      .getAll({
        page: pageDemandee,
        size: TAILLE_PAGE,
        categorie: this.categorieSelectionnee(),
        utilisateurId: this.auth.currentUser()?.id,
      })
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

  protected selectionner(nom: string | null): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { categorie: nom, page: null },
      queryParamsHandling: 'merge',
    });
  }
}
