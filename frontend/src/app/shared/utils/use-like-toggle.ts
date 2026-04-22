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

export function useLikeToggle(article: Signal<Article | null | undefined>): LikeToggle {
  const articleService = inject(ArticleService);
  const auth = inject(AuthService);
  const router = inject(Router);

  const nbLikes = signal(0);
  const aLike = signal(false);
  const enCours = signal(false);

  effect(() => {
    const a = article();
    const userId = auth.currentUser()?.id;
    nbLikes.set(a?.likes?.length ?? 0);
    aLike.set(!!userId && !!a?.likes?.some((u) => u.id === userId));
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
      },
      error: () => enCours.set(false),
    });
  }

  return { nbLikes, aLike, enCours, toggle };
}
