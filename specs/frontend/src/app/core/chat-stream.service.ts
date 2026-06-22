import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Client, IMessage } from '@stomp/stompjs';
import { Subject } from 'rxjs';
import { ChatMessageRequest, ChatMessageResponse } from './chat.models';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class ChatStreamService {
  private client: Client | null = null;
  private readonly messagesSubject = new Subject<ChatMessageResponse>();
  readonly messages$ = this.messagesSubject.asObservable();

  constructor(private readonly authService: AuthService) {}

  connect(): void {
    if (this.client?.active) {
      return;
    }

    const wsUrl = resolveWebSocketUrl(environment.wsBaseUrl);
    this.client = new Client({
      brokerURL: wsUrl,
      reconnectDelay: 3000,
      debug: () => undefined,
      connectHeaders: {
        Authorization: `Bearer ${this.authService.authState().token ?? ''}`,
      },
      onConnect: () => {
        this.client?.subscribe('/user/queue/reply', (message: IMessage) => {
          this.messagesSubject.next(JSON.parse(message.body) as ChatMessageResponse);
        });
      },
    });

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
  }

  sendMessage(request: ChatMessageRequest): void {
    if (!this.client?.connected) {
      throw new Error('Chat stream is not connected');
    }

    this.client.publish({
      destination: '/app/chat',
      body: JSON.stringify(request),
    });
  }
}

function resolveWebSocketUrl(baseUrl: string): string {
  if (baseUrl) {
    return `${baseUrl}/ws`;
  }

  const origin = window.location.origin.replace(/^http/, 'ws');
  return `${origin}/ws`;
}
