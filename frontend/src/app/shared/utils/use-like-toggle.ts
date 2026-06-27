import { Signal, effect, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ArticleService } from '../../core/services/article.service';
import { AuthService } from '../../core/services/auth.service';
import { Article } from '../../models/article.model';

export interface LikeToggle {
  nbLikes: Signal<number>;
  aLike: Signal<boolean>;
  enCours: Signal<boolean>;
  toggle: () => void;
}

export interface LikeToggleOptions {
  onToggled?: (liked: boolean) => void;
}

export function useLikeToggle(
  article: Signal<Article | null | undefined>,
  options?: LikeToggleOptions,
): LikeToggle {
  const articleService = inject(ArticleService);
  const auth = inject(AuthService);
  const router = inject(Router);

  const nbLikes = signal(0);
  const aLike = signal(false);
  const enCours = signal(false);

  effect(() => {
    const a = article();
    nbLikes.set(a?.nbLikes ?? 0);
    aLike.set(a?.aimeParUtilisateurCourant ?? false);
  });

  function toggle(): void {
    const a = article();
    if (!a) return;
    const user = auth.currentUser();
    if (!user) {
      router.navigateByUrl('/login');
      return;
    }
    if (enCours()) return;
    enCours.set(true);
    articleService.toggleLike(a.id, user.id).subscribe({
      next: (etat) => {
        nbLikes.set(etat.nombreLikes);
        aLike.set(etat.likeParUtilisateur);
        enCours.set(false);
        options?.onToggled?.(etat.likeParUtilisateur);
      },
      error: () => enCours.set(false),
    });
  }

  return { nbLikes, aLike, enCours, toggle };
}

