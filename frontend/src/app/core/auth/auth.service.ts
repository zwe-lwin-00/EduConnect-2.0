import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { AuthRoutes, Roles, ROLE_HOME } from '../../shared/constants/auth.constants';

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: {
    id: string;
    email: string;
    fullName: string;
    roles: string[];
    mustChangePassword: boolean;
  };
}

export interface UserInfo {
  id: string;
  email: string;
  fullName: string;
  roles: string[];
  mustChangePassword: boolean;
}

const TOKEN_KEY = 'educonnect_access_token';
const REFRESH_KEY = 'educonnect_refresh_token';
const USER_KEY = 'educonnect_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiUrl}/auth`;
  private currentUser$ = new BehaviorSubject<UserInfo | null>(this.getStoredUser());

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.api}/login`, { email, password }).pipe(
      tap((res) => {
        this.setToken(res.accessToken);
        this.setRefreshToken(res.refreshToken);
        this.setStoredUser(res.user);
        this.currentUser$.next(res.user);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    this.currentUser$.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  getStoredUser(): UserInfo | null {
    try {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }

  get currentUser(): Observable<UserInfo | null> {
    return this.currentUser$.asObservable();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  hasRole(role: string): boolean {
    const user = this.getStoredUser();
    return user?.roles?.includes(role) ?? false;
  }

  me(): Observable<UserInfo | null> {
    return this.http.get<UserInfo>(`${this.api}/me`).pipe(
      tap((u) => {
        this.setStoredUser(u);
        this.currentUser$.next(u);
      }),
      catchError(() => {
        this.logout();
        return of(null);
      })
    );
  }

  /** Role-based redirect: Admin → /admin, Teacher → /teacher, Parent → /parent. */
  getRedirectByRole(): string {
    const user = this.getStoredUser();
    if (!user?.roles?.length) return AuthRoutes.LOGIN;
    if (user.roles.includes(Roles.ADMIN)) return AuthRoutes.ADMIN_HOME;
    if (user.roles.includes(Roles.TEACHER)) return AuthRoutes.TEACHER_HOME;
    if (user.roles.includes(Roles.PARENT)) return AuthRoutes.PARENT_HOME;
    return AuthRoutes.LOGIN;
  }

  /** Get home route for a role (for returnUrl checks). */
  getHomeForRole(role: string): string {
    return ROLE_HOME[role as keyof typeof ROLE_HOME] ?? AuthRoutes.LOGIN;
  }

  private setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  private setRefreshToken(token: string): void {
    localStorage.setItem(REFRESH_KEY, token);
  }

  private setStoredUser(user: UserInfo): void {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  }
}
