import { Component, computed, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { combineLatest, map, of, switchMap } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Header } from '../../shared/components/header/header';
import { ArticleCard } from '../../shared/components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';
import { AuthService } from '../../core/services/auth.service';
import { FollowedCategoriesService } from '../../core/services/followed-categories.service';
import { Article } from '../../models/article.model';
import { paletteFor } from '../../shared/constants/category-palettes';
import { useLikeToggle } from '../../shared/utils/use-like-toggle';

type Etat = {
  article: Article | null;
  similaires: Article[];
  chargement: boolean;
  erreur: boolean;
};

const ETAT_INITIAL: Etat = { article: null, similaires: [], chargement: true, erreur: false };

@Component({
  selector: 'app-article-detail',
  imports: [Header, ArticleCard, RouterLink],
  templateUrl: './article-detail.html',
})
export class ArticleDetail {
  private readonly articleService = inject(ArticleService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly followedCategories = inject(FollowedCategoriesService);

  readonly id = input.required<string>();
  private readonly id$ = toObservable(this.id);

  protected readonly etat = toSignal(
    this.id$.pipe(
      switchMap((idStr) => {
        const idNum = Number(idStr);
        if (!Number.isFinite(idNum)) {
          return of<Etat>({ article: null, similaires: [], chargement: false, erreur: true });
        }
        return combineLatest([
          this.articleService.getById(idNum),
          this.articleService.getAll(),
        ]).pipe(
          map(([article, tous]) => ({
            article,
            similaires: tous
              .filter((a) => a.id !== article.id && a.categorie?.id === article.categorie?.id)
              .slice(0, 3),
            chargement: false,
            erreur: false,
          })),
          catchError(() => of<Etat>({ article: null, similaires: [], chargement: false, erreur: true })),
        );
      }),
    ),
    { initialValue: ETAT_INITIAL },
  );

  protected readonly article = computed(() => this.etat().article);

  protected readonly palette = computed(() => paletteFor(this.article()?.categorie?.nomCategorie));

  protected readonly tempsLecture = computed(() => {
    const mots = this.article()?.contenu?.split(/\s+/).length ?? 0;
    return Math.max(1, Math.round(mots / 200));
  });

  protected readonly like = useLikeToggle(this.article);

  protected readonly initiales = computed(() => {
    const nom = this.article()?.auteur?.nom ?? '';
    return nom.split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase();
  });

  protected readonly dateFormatee = computed(() => {
    const iso = this.article()?.createdAt;
    if (!iso) return '';
    return new Date(iso).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  });

  protected readonly paragraphes = computed(() => {
    const contenu = this.article()?.contenu ?? '';
    return contenu.split(/\n{2,}/).map((p) => p.trim()).filter(Boolean);
  });

  protected readonly estSuivie = computed(() => {
    const cat = this.article()?.categorie;
    return cat ? this.followedCategories.estSuivie(cat.id) : false;
  });

  protected toggleFollow(): void {
    if (!this.auth.estConnecte()) {
      this.router.navigate(['/login']);
      return;
    }
    const cat = this.article()?.categorie;
    if (cat) this.followedCategories.toggle(cat);
  }
}
