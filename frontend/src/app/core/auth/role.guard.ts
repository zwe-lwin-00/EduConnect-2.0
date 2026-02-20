import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router, UrlTree } from '@angular/router';
import { AuthService } from './auth.service';
import { AuthRoutes } from '../../shared/constants/auth.constants';

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
    return this.router.createUrlTree([AuthRoutes.LOGIN], { queryParams: { unauthorized: '1' } });
  }
}
