import { Injectable, computed, effect, signal } from '@angular/core';
import { AuthSession } from '../types/auth-session';

const STORAGE_KEY = 'auth.session';

function loadSession(): AuthSession | null {
  const raw = sessionStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthSession;
  } catch {
    return null;
  }
}

@Injectable({ providedIn: 'root' })
export class AuthState {
  private readonly _session = signal<AuthSession | null>(loadSession());

  readonly session = computed(() => this._session());
  readonly accessToken = computed(() => this._session()?.accessToken ?? null);
  readonly isAdmin = computed(() => this._session()?.isAdmin ?? false);
  readonly isLoggedIn = computed(() => !!this.accessToken());

  constructor() {
    effect(() => {
      const s = this._session();
      if (s) sessionStorage.setItem(STORAGE_KEY, JSON.stringify(s));
      else sessionStorage.removeItem(STORAGE_KEY);
    });
  }

  setSession(session: AuthSession): void {
    this._session.set(session);
  }

  clear(): void {
    this._session.set(null);
  }
}
