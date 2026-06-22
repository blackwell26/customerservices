import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { ChatTurn } from '../../core/chat.models';
import { ChatStreamService } from '../../core/chat-stream.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <section class="page page--chat">
      <header class="topbar">
        <div>
          <p class="eyebrow">Signed in as</p>
          <h1>{{ username() || 'Guest' }}</h1>
        </div>
        <button type="button" (click)="logout()">Log out</button>
      </header>

      <div class="layout">
        <aside class="card sidebar">
          <h2>Session</h2>
          <p>JWT-backed session storage is active.</p>
          <p class="meta">Roles: {{ roles().join(', ') || 'none' }}</p>
          <p class="meta">Session ID: {{ sessionId() || 'new' }}</p>
        </aside>

        <main class="card chat">
          <h2>Customer Service Chat</h2>
          <div class="messages">
            <div
              class="message"
              *ngFor="let entry of transcript()"
              [class.message--bot]="entry.role === 'assistant'"
              [class.message--user]="entry.role === 'user'"
            >
              {{ entry.text }}
            </div>
          </div>
          <form class="composer" [formGroup]="form" (ngSubmit)="submit()">
            <input type="text" placeholder="Type a message..." formControlName="message" />
            <button type="submit" [disabled]="form.invalid || busy">
              {{ busy ? 'Sending...' : 'Send' }}
            </button>
          </form>
          <p class="error" *ngIf="error()">{{ error() }}</p>
        </main>
      </div>
    </section>
  `,
})
export class ChatComponent {
  private readonly fb = inject(FormBuilder);
  private readonly chatStreamService = inject(ChatStreamService);
  private readonly authService = inject(AuthService);
  private readonly subscription: Subscription;

  username = signal(this.authService.authState().username ?? '');
  roles = signal(this.authService.authState().roles);
  sessionId = signal<string | null>(null);
  transcript = signal<ChatTurn[]>([
    { role: 'assistant', text: 'Hello. I can help with orders, refunds, policies, and product questions.' },
  ]);
  busy = false;
  error = signal('');

  form = this.fb.nonNullable.group({
    message: ['', Validators.required],
  });

  constructor() {
    this.chatStreamService.connect();
    this.subscription = this.chatStreamService.messages$.subscribe({
      next: (response) => {
        this.sessionId.set(response.sessionId);
        this.transcript.set(response.history);
        this.busy = false;
      },
    });
  }

  logout() {
    this.authService.logout();
  }

  submit() {
    if (this.form.invalid || this.busy) {
      return;
    }

    const message = this.form.getRawValue().message.trim();
    if (!message) {
      return;
    }

    this.busy = true;
    this.error.set('');
    this.transcript.update((items) => [...items, { role: 'user', text: message }]);

    try {
      this.chatStreamService.sendMessage({ message, sessionId: this.sessionId() });
      this.form.reset({ message: '' });
    } catch {
      this.busy = false;
      this.error.set('Chat stream is unavailable. Check backend connectivity.');
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
    this.chatStreamService.disconnect();
  }
}
