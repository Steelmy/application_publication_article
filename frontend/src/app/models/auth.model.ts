import { TypeRole } from './role.model';

export interface InscriptionDTO {
  nom: string;
  email: string;
  motDePasse: string;
  role: TypeRole;
}

export interface ConnexionDTO {
  email: string;
  motDePasse: string;
}
