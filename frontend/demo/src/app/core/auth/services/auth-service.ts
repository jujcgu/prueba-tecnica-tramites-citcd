import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { AuthState } from '../states/auth-state';
import { LoginRequest } from '../types/login-request';
import { LoginResponse } from '../types/login-response';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly authState = inject(AuthState);

  login(body: LoginRequest) {
    return this.http
      .post<LoginResponse>('/api/auth/login', body)
      .pipe(
        tap((res) =>
          this.authState.setSession({ accessToken: res.accessToken, isAdmin: res.isAdmin })
        )
      );
  }

  logout(): void {
    this.authState.clear();
  }
}
