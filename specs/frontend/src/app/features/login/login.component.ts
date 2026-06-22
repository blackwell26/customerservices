import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="page page--login">
      <div class="hero">
        <p class="eyebrow">Customer service platform</p>
        <h1>Sign in to start a support session</h1>
        <p class="lede">
          Authenticate to access chat, order tracking, and escalation workflows backed by the local Spring service.
        </p>
      </div>

      <form class="card" [formGroup]="form" (ngSubmit)="submit()">
        <label>
          Username
          <input type="text" formControlName="username" autocomplete="username" />
        </label>

        <label>
          Password
          <input type="password" formControlName="password" autocomplete="current-password" />
        </label>

        <button type="submit" [disabled]="form.invalid || busy">
          {{ busy ? 'Signing in...' : 'Sign in' }}
        </button>

        <p class="error" *ngIf="error">{{ error }}</p>

        <p class="hint">
          Default backend credentials: <code>admin / admin</code>
        </p>
      </form>
    </section>
  `,
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  busy = false;
  error = '';

  form = this.fb.nonNullable.group({
    username: ['admin', Validators.required],
    password: ['admin', Validators.required],
  });

  submit() {
    if (this.form.invalid || this.busy) {
      return;
    }

    this.busy = true;
    this.error = '';

    this.authService.login(this.form.getRawValue()).subscribe({
      next: (response) => {
        this.authService.setSession(response);
        this.busy = false;
        this.router.navigateByUrl('/chat');
      },
      error: () => {
        this.busy = false;
        this.error = 'Login failed. Check backend availability and credentials.';
      },
    });
  }
}

