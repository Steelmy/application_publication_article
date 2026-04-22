import { Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Article } from '../../../models/article.model';
import { paletteFor } from '../../constants/category-palettes';
import { useLikeToggle } from '../../utils/use-like-toggle';

@Component({
  selector: 'app-article-card',
  imports: [RouterLink],
  templateUrl: './article-card.html',
})
export class ArticleCard {
  readonly article = input.required<Article>();

  protected readonly palette = computed(() => paletteFor(this.article().categorie?.nomCategorie));

  protected readonly tempsLecture = computed(() => {
    const mots = this.article().contenu?.split(/\s+/).length ?? 0;
    return Math.max(1, Math.round(mots / 200));
  });

  protected readonly dateFormatee = computed(() => {
    const iso = this.article().createdAt;
    if (!iso) return '';
    return new Date(iso).toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' });
  });

  protected readonly like = useLikeToggle(this.article);

  protected onLikeClick(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.like.toggle();
  }
}
