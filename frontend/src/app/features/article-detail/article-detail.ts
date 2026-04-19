import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { combineLatest, map, of, switchMap } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Header } from '../../shared/components/header/header';
import { ArticleCard } from '../../shared/components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';
import { AuthService } from '../../core/services/auth.service';
import { Article } from '../../models/article.model';

type Etat = {
  article: Article | null;
  similaires: Article[];
  chargement: boolean;
  erreur: boolean;
};

const PALETTES: Record<string, { gradient: string; chipBg: string; chipText: string; accent: string }> = {
  Sciences: { gradient: 'from-emerald-300 via-teal-400 to-cyan-500', chipBg: 'bg-emerald-100', chipText: 'text-emerald-800', accent: 'text-emerald-700' },
  Littérature: { gradient: 'from-violet-300 via-purple-400 to-fuchsia-500', chipBg: 'bg-violet-100', chipText: 'text-violet-800', accent: 'text-violet-700' },
  Philosophie: { gradient: 'from-sky-300 via-indigo-400 to-blue-500', chipBg: 'bg-indigo-100', chipText: 'text-indigo-800', accent: 'text-indigo-700' },
  Histoire: { gradient: 'from-orange-300 via-amber-400 to-yellow-500', chipBg: 'bg-amber-100', chipText: 'text-amber-800', accent: 'text-amber-700' },
  Société: { gradient: 'from-rose-300 via-pink-400 to-fuchsia-500', chipBg: 'bg-rose-100', chipText: 'text-rose-800', accent: 'text-rose-700' },
  Technologie: { gradient: 'from-zinc-700 via-zinc-800 to-zinc-900', chipBg: 'bg-zinc-100', chipText: 'text-zinc-800', accent: 'text-zinc-700' },
  Arts: { gradient: 'from-amber-300 via-rose-400 to-fuchsia-500', chipBg: 'bg-rose-100', chipText: 'text-rose-800', accent: 'text-rose-700' },
};

const DEFAULT_PALETTE = { gradient: 'from-zinc-300 via-zinc-400 to-zinc-500', chipBg: 'bg-zinc-100', chipText: 'text-zinc-800', accent: 'text-zinc-700' };

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

  protected readonly palette = computed(() => {
    const nom = this.etat().article?.categorie?.nomCategorie;
    return (nom && PALETTES[nom]) || DEFAULT_PALETTE;
  });

  protected readonly tempsLecture = computed(() => {
    const mots = this.etat().article?.contenu?.split(/\s+/).length ?? 0;
    return Math.max(1, Math.round(mots / 200));
  });

  protected readonly nbLikes = signal(0);
  protected readonly aLike = signal(false);
  protected readonly likeEnCours = signal(false);

  // Sync l'état local des likes quand l'article change
  private readonly _syncLikes = effect(() => {
    const article = this.etat().article;
    if (!article) return;
    const userId = this.auth.currentUser()?.id;
    this.nbLikes.set(article.likes?.length ?? 0);
    this.aLike.set(!!userId && (article.likes?.some((u) => u.id === userId) ?? false));
  });

  protected toggleLike(): void {
    const article = this.etat().article;
    const user = this.auth.currentUser();
    if (!article) return;
    if (!user) {
      this.router.navigateByUrl('/login');
      return;
    }
    if (this.likeEnCours()) return;
    this.likeEnCours.set(true);
    this.articleService.toggleLike(article.id, user.id).subscribe({
      next: (etat) => {
        this.nbLikes.set(etat.nombreLikes);
        this.aLike.set(etat.likeParUtilisateur);
        this.likeEnCours.set(false);
      },
      error: () => this.likeEnCours.set(false),
    });
  }

  protected readonly initiales = computed(() => {
    const nom = this.etat().article?.auteur?.nom ?? '';
    return nom.split(' ').map((p) => p[0]).slice(0, 2).join('').toUpperCase();
  });

  protected readonly dateFormatee = computed(() => {
    const iso = this.etat().article?.createdAt;
    if (!iso) return '';
    return new Date(iso).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  });

  protected readonly paragraphes = computed(() => {
    const contenu = this.etat().article?.contenu ?? '';
    return contenu.split(/\n{2,}/).map((p) => p.trim()).filter(Boolean);
  });
}
