import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { AuthRoutes } from '../../shared/constants/auth.constants';
import { getApiBase } from '../services/api-url';

/** API error body shape from backend GlobalExceptionHandler. */
export interface ApiErrorBody {
  error?: string;
  message?: string;
  status?: number;
  details?: string[];
  path?: string;
}

/**
 * Error interceptor: handle 401/403 globally (logout + redirect), rethrow so callers can still handle.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((err: HttpErrorResponse) => {
        if (!err.url?.startsWith(getApiBase())) {
          return throwError(() => err);
        }

        const status = err.status;
        const body: ApiErrorBody = err.error;

        if (status === 401) {
          this.auth.logout();
          const returnUrl = this.router.url || '/';
          this.router.navigate([AuthRoutes.LOGIN], { queryParams: { returnUrl } });
          return throwError(() => err);
        }

        if (status === 403) {
          if (this.auth.isAuthenticated()) {
            const home = this.auth.getRedirectByRole();
            this.router.navigate([home], { queryParams: { unauthorized: '1' } });
          } else {
            this.auth.logout();
            this.router.navigate([AuthRoutes.LOGIN], { queryParams: { returnUrl: this.router.url } });
          }
          return throwError(() => err);
        }

        return throwError(() => err);
      })
    );
  }
}
