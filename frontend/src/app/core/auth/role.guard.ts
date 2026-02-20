import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { AuthRoutes, ROLE_HOME } from '../../shared/constants/auth.constants';

/**
 * Role guard: must run after AuthGuard. Allows access if user has one of the route's allowed roles.
 * If authenticated but wrong role, redirects to the user's role home (e.g. /admin) instead of login.
 */
@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree {
    const allowedRoles = route.data['roles'] as string[] | undefined;
    if (!allowedRoles?.length) return true;
    const hasRole = allowedRoles.some((r) => this.auth.hasRole(r));
    if (hasRole) return true;
    const user = this.auth.getStoredUser();
    const roleHome = user?.roles?.[0] ? ROLE_HOME[user.roles[0] as keyof typeof ROLE_HOME] : null;
    if (roleHome) {
      return this.router.createUrlTree([roleHome], { queryParams: { unauthorized: '1' } });
    }
    return this.router.createUrlTree([AuthRoutes.LOGIN], { queryParams: { returnUrl: state.url, unauthorized: '1' } });
  }
}
