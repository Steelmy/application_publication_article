import { Role } from './role.model';

export interface Utilisateur {
  id: number;
  nom: string;
  email: string;
  role: Role;
  createdAt?: string;
  updateAt?: string;
}
