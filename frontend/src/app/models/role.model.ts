export enum TypeRole {
  ADMIN = 'ADMIN',
  REDACTEUR = 'REDACTEUR',
  UTILISATEUR = 'UTILISATEUR',
}

export interface Role {
  id: number;
  nomRole: TypeRole;
}
