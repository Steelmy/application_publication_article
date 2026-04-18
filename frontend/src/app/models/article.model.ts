import { Categorie } from './categorie.model';
import { Utilisateur } from './utilisateur.model';

export interface Article {
  id: number;
  titre: string;
  resume: string;
  contenu: string;
  nombreDeVues: number;
  categorie: Categorie;
  auteur: Utilisateur;
  createdAt?: string;
  updateAt?: string;
}

export interface ArticleCreateDTO {
  titre: string;
  resume: string;
  contenu: string;
  auteurId: number;
  categorieId: number;
}
