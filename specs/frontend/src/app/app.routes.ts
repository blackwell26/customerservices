import { Routes } from '@angular/router';
import { ChatComponent } from './features/chat/chat.component';
import { LoginComponent } from './features/login/login.component';
import { authGuard } from './core/auth.guard';

export const appRoutes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'chat', component: ChatComponent, canActivate: [authGuard] },
];
