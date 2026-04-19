import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../../core/services/auth.service';
import { TypeRole } from '../../../models/role.model';

function motsDePasseIdentiques(group: AbstractControl): ValidationErrors | null {
  const mdp = group.get('motDePasse')?.value;
  const confirmation = group.get('confirmation')?.value;
  return mdp && confirmation && mdp !== confirmation ? { mismatch: true } : null;
}

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
})
export class Register {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group(
    {
      nom: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      motDePasse: ['', [Validators.required, Validators.minLength(6)]],
      confirmation: ['', [Validators.required]],
    },
    { validators: motsDePasseIdentiques },
  );

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const { nom, email, motDePasse } = this.form.getRawValue();

    this.auth.inscrire({ nom, email, motDePasse, role: TypeRole.UTILISATEUR }).subscribe({
      next: () => this.router.navigateByUrl('/'),
      error: (err: HttpErrorResponse) => {
        // Le back renvoie 400 + message texte si l'email est déjà pris
        this.errorMessage.set(
          err.status === 400 && typeof err.error === 'string'
            ? err.error
            : 'Une erreur est survenue. Réessayez.',
        );
        this.loading.set(false);
      },
    });
  }
}
