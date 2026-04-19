import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
})
export class Header {
  protected readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly initiales = computed(() => {
    const user = this.auth.currentUser();
    if (!user) return '';
    return user.nom
      .split(' ')
      .map((p) => p[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  });

  protected seDeconnecter(): void {
    this.auth.seDeconnecter();
    this.router.navigateByUrl('/');
  }
}
