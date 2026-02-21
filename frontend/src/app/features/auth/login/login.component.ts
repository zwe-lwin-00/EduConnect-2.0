import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthRoutes, Roles } from '../../../shared/constants/auth.constants';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  form: FormGroup;
  loading = false;
  error = '';
  returnUrl: string;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || this.auth.getRedirectByRole();
  }

  onEmailChange(value: string): void {
    this.form.get('email')?.setValue(value ?? '');
    this.form.get('email')?.markAsTouched();
  }

  onPasswordChange(value: string): void {
    this.form.get('password')?.setValue(value ?? '');
    this.form.get('password')?.markAsTouched();
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;
    this.error = '';
    this.auth.login(this.form.value.email, this.form.value.password).subscribe({
      next: () => {
        this.loading = false;
        const roleHome = this.auth.getRedirectByRole();
        const role = this.roleFromPath(this.returnUrl);
        if (this.returnUrl && this.returnUrl !== AuthRoutes.LOGIN && role && this.auth.hasRole(role)) {
          this.router.navigateByUrl(this.returnUrl);
        } else {
          this.router.navigateByUrl(roleHome);
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.error || 'Login failed. Check your email and password.';
      }
    });
  }

  private roleFromPath(path: string): string {
    if (path.startsWith(AuthRoutes.ADMIN_HOME)) return Roles.ADMIN;
    if (path.startsWith(AuthRoutes.TEACHER_HOME)) return Roles.TEACHER;
    if (path.startsWith(AuthRoutes.PARENT_HOME)) return Roles.PARENT;
    return '';
  }
}
