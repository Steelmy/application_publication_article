import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Article } from '../../../models/article.model';
import { ArticleService } from '../../../core/services/article.service';
import { AuthService } from '../../../core/services/auth.service';

type Palette = {
  gradient: string;
  accent: string;
};

const PALETTES: Record<string, Palette> = {
  Sciences: { gradient: 'from-emerald-300 via-teal-400 to-cyan-500', accent: 'text-emerald-700' },
  Littérature: { gradient: 'from-violet-300 via-purple-400 to-fuchsia-500', accent: 'text-violet-700' },
  Philosophie: { gradient: 'from-sky-300 via-indigo-400 to-blue-500', accent: 'text-indigo-700' },
  Histoire: { gradient: 'from-orange-300 via-amber-400 to-yellow-500', accent: 'text-amber-700' },
  Société: { gradient: 'from-rose-300 via-pink-400 to-fuchsia-500', accent: 'text-rose-700' },
  Technologie: { gradient: 'from-zinc-700 via-zinc-800 to-zinc-900', accent: 'text-zinc-700' },
  Arts: { gradient: 'from-amber-300 via-rose-400 to-fuchsia-500', accent: 'text-rose-700' },
};

const DEFAULT_PALETTE: Palette = {
  gradient: 'from-zinc-300 via-zinc-400 to-zinc-500',
  accent: 'text-zinc-700',
};

@Component({
  selector: 'app-article-card',
  imports: [RouterLink],
  templateUrl: './article-card.html',
})
export class ArticleCard {
  private readonly articleService = inject(ArticleService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly article = input.required<Article>();

  protected readonly palette = computed<Palette>(() => {
    const nom = this.article().categorie?.nomCategorie;
    return (nom && PALETTES[nom]) || DEFAULT_PALETTE;
  });

  protected readonly tempsLecture = computed(() => {
    const mots = this.article().contenu?.split(/\s+/).length ?? 0;
    return Math.max(1, Math.round(mots / 200));
  });

  protected readonly nbLikes = signal(0);
  protected readonly aLike = signal(false);
  protected readonly likeEnCours = signal(false);

  private readonly _syncLikes = effect(() => {
    const a = this.article();
    const userId = this.auth.currentUser()?.id;
    this.nbLikes.set(a.likes?.length ?? 0);
    this.aLike.set(!!userId && (a.likes?.some((u) => u.id === userId) ?? false));
  });

  protected readonly dateFormatee = computed(() => {
    const iso = this.article().createdAt;
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
  });

  protected toggleLike(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    const user = this.auth.currentUser();
    if (!user) {
      this.router.navigateByUrl('/login');
      return;
    }
    if (this.likeEnCours()) return;
    this.likeEnCours.set(true);
    this.articleService.toggleLike(this.article().id, user.id).subscribe({
      next: (etat) => {
        this.nbLikes.set(etat.nombreLikes);
        this.aLike.set(etat.likeParUtilisateur);
        this.likeEnCours.set(false);
      },
      error: () => this.likeEnCours.set(false),
    });
  }
}
