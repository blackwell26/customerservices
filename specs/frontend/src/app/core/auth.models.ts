export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  roles: string[];
}

export interface AuthState {
  token: string | null;
  username: string | null;
  roles: string[];
}

