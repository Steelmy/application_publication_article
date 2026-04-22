import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { CategorieService } from './categorie.service';
import { AuthService } from './auth.service';
import { Categorie } from '../../models/categorie.model';

@Injectable({ providedIn: 'root' })
export class FollowedCategoriesService {
  private readonly categorieService = inject(CategorieService);
  private readonly auth = inject(AuthService);

  private readonly _liste = signal<Categorie[]>([]);
  private readonly _enCours = signal(false);

  readonly liste = this._liste.asReadonly();
  readonly enCours = this._enCours.asReadonly();
  readonly ids = computed(() => new Set(this._liste().map((c) => c.id)));

  constructor() {
    effect(() => {
      const user = this.auth.currentUser();
      if (!user) {
        this._liste.set([]);
        return;
      }
      this.categorieService.getCategoriesSuivies(user.id).subscribe({
        next: (cats) => this._liste.set(cats),
        error: () => this._liste.set([]),
      });
    });
  }

  estSuivie(categorieId: number): boolean {
    return this.ids().has(categorieId);
  }

  toggle(categorie: Categorie): void {
    const user = this.auth.currentUser();
    if (!user || this._enCours()) return;
    this._enCours.set(true);
    this.categorieService.toggleFollow(categorie.id, user.id).subscribe({
      next: (etat) => {
        if (etat.suivieParUtilisateur) {
          this._liste.update((l) => (l.some((c) => c.id === categorie.id) ? l : [...l, categorie]));
        } else {
          this._liste.update((l) => l.filter((c) => c.id !== categorie.id));
        }
        this._enCours.set(false);
      },
      error: () => this._enCours.set(false),
    });
  }
}
