export interface ChatMessageRequest {
  message: string;
  sessionId?: string | null;
}

export interface ChatTurn {
  role: 'user' | 'assistant';
  text: string;
}

export interface ChatMessageResponse {
  sessionId: string;
  userMessage: string;
  assistantMessage: string;
  history: ChatTurn[];
}

