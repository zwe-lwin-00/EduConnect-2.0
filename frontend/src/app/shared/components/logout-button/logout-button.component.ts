import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthRoutes } from '../../constants/auth.constants';

/**
 * Reusable logout button: clears JWT/user and redirects to login.
 */
@Component({
  selector: 'app-logout-button',
  template: `
    <button type="button" class="logout-btn" (click)="logout()">
      Logout
    </button>
  `,
  styleUrls: ['./logout-button.component.css']
})
export class LogoutButtonComponent {
  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate([AuthRoutes.LOGIN]);
  }
}
