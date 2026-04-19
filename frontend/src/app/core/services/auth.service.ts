import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Utilisateur } from '../../models/utilisateur.model';
import { ConnexionDTO, InscriptionDTO } from '../../models/auth.model';
import { TypeRole } from '../../models/role.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly api = `${environment.apiUrl}/utilisateurs`;
  private readonly STORAGE_KEY = 'inkwell_user';

  // --- Etat réactif via signal ---
  private readonly _currentUser = signal<Utilisateur | null>(this.lireUserStocke());

  readonly currentUser = this._currentUser.asReadonly();
  readonly estConnecte = computed(() => this._currentUser() !== null);
  readonly estAdmin = computed(() => this._currentUser()?.role?.nomRole === TypeRole.ADMIN);

  // --- Actions ---
  inscrire(dto: InscriptionDTO): Observable<Utilisateur> {
    return this.http
      .post<Utilisateur>(`${this.api}/inscription`, dto)
      .pipe(tap((user) => this.persisterUser(user)));
  }

  seConnecter(dto: ConnexionDTO): Observable<Utilisateur> {
    return this.http
      .post<Utilisateur>(`${this.api}/connexion`, dto)
      .pipe(tap((user) => this.persisterUser(user)));
  }

  seDeconnecter(): void {
    localStorage.removeItem(this.STORAGE_KEY);
    this._currentUser.set(null);
  }

  // --- Persistance locale ---
  private persisterUser(user: Utilisateur): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(user));
    this._currentUser.set(user);
  }

  private lireUserStocke(): Utilisateur | null {
    const raw = localStorage.getItem(this.STORAGE_KEY);
    return raw ? (JSON.parse(raw) as Utilisateur) : null;
  }
}
