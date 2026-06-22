import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse, AuthState } from './auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly state = signal<AuthState>({
    token: localStorage.getItem('auth_token'),
    username: localStorage.getItem('auth_username'),
    roles: JSON.parse(localStorage.getItem('auth_roles') ?? '[]'),
  });

  constructor(private readonly http: HttpClient, private readonly router: Router) {}

  readonly authState = this.state.asReadonly();

  login(request: LoginRequest) {
    return this.http.post<LoginResponse>(`${resolveApiBaseUrl(environment.apiBaseUrl)}/api/v1/auth/login`, request);
  }

  setSession(response: LoginResponse) {
    localStorage.setItem('auth_token', response.token);
    localStorage.setItem('auth_username', response.username);
    localStorage.setItem('auth_roles', JSON.stringify(response.roles));
    this.state.set({
      token: response.token,
      username: response.username,
      roles: response.roles,
    });
  }

  logout() {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_username');
    localStorage.removeItem('auth_roles');
    this.state.set({ token: null, username: null, roles: [] });
    this.router.navigateByUrl('/');
  }
}

function resolveApiBaseUrl(baseUrl: string): string {
  return baseUrl || '';
}
