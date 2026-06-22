import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  template: `
    <div class="shell">
      <nav class="nav">
        <a routerLink="/">Login</a>
        <a routerLink="/chat">Chat</a>
        <span class="session" *ngIf="signedIn()">{{ username() }}</span>
      </nav>
      <router-outlet />
    </div>
  `,
})
export class AppComponent {
  private readonly authService = inject(AuthService);

  signedIn = computed(() => Boolean(this.authService.authState().token));
  username = computed(() => this.authService.authState().username ?? '');
}
