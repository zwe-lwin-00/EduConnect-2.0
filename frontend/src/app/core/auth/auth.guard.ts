import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { AuthRoutes } from '../../shared/constants/auth.constants';

/**
 * Auth guard: allows access only when the user has a valid token (is authenticated).
 * Redirects to login with returnUrl so the user can be sent back after signing in.
 */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    if (this.auth.isAuthenticated()) {
      return true;
    }
    const returnUrl = state.url || '/';
    return this.router.createUrlTree([AuthRoutes.LOGIN], { queryParams: { returnUrl } });
  }
}
