import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ChatMessageRequest, ChatMessageResponse } from './chat.models';

@Injectable({ providedIn: 'root' })
export class ChatService {
  constructor(private readonly http: HttpClient) {}

  sendMessage(request: ChatMessageRequest) {
    return this.http.post<ChatMessageResponse>(`${environment.apiBaseUrl}/api/v1/chat/messages`, request);
  }
}

