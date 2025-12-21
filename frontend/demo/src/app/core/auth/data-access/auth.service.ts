import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { AuthState } from '../states/auth.state';
import { LoginRequestModel } from '../models/login-request.model';
import { LoginResponseModel } from '../models/login-response.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly authState = inject(AuthState);

  login(body: LoginRequestModel) {
    return this.http
      .post<LoginResponseModel>('/api/auth/login', body)
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
