export interface AuteurResume {
  id: number;
  nom: string;
}

export interface CategorieResume {
  id: number;
  nomCategorie: string;
}

export interface Article {
  id: number;
  titre: string;
  resume: string;
  contenu: string;
  nombreDeVues: number;
  nbLikes: number;
  aimeParUtilisateurCourant: boolean;
  categorie: CategorieResume;
  auteur: AuteurResume;
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

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
