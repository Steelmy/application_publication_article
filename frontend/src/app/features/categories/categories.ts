import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { map, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Header } from '../../shared/components/header/header';
import { CategorieAvecCompteur, CategorieService } from '../../core/services/categorie.service';
import { AuthService } from '../../core/services/auth.service';
import { FollowedCategoriesService } from '../../core/services/followed-categories.service';
import { Categorie } from '../../models/categorie.model';
import { paletteFor } from '../../shared/constants/category-palettes';

type Etat = {
  categories: CategorieAvecCompteur[];
  chargement: boolean;
  erreur: boolean;
};

const ETAT_INITIAL: Etat = { categories: [], chargement: true, erreur: false };

@Component({
  selector: 'app-categories',
  imports: [Header],
  templateUrl: './categories.html',
})
export class Categories {
  private readonly categorieService = inject(CategorieService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly followedCategories = inject(FollowedCategoriesService);
  protected readonly paletteFor = paletteFor;

  protected readonly etat = toSignal(
    this.categorieService.getAllAvecCompteurs().pipe(
      map((categories) => ({ categories, chargement: false, erreur: false }) as Etat),
      catchError(() => of<Etat>({ categories: [], chargement: false, erreur: true })),
    ),
    { initialValue: ETAT_INITIAL },
  );

  protected readonly nbSuivies = computed(() => this.followedCategories.liste().length);

  protected toggleFollow(categorie: Categorie, event: Event): void {
    event.stopPropagation();
    if (!this.auth.estConnecte()) {
      this.router.navigate(['/login']);
      return;
    }
    this.followedCategories.toggle(categorie);
  }

  protected ouvrir(categorie: Categorie): void {
    this.router.navigate(['/'], { queryParams: { categorie: categorie.nomCategorie } });
  }
}
