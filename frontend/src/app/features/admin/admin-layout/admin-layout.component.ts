import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

export interface NavItem {
  id: string;
  text: string;
  path: string;
  selected?: boolean;
}

const NAV_LINKS: Omit<NavItem, 'selected'>[] = [
  { id: 'dashboard', text: 'Dashboard', path: '/admin' },
  { id: 'teachers', text: 'Teachers', path: '/admin/teachers' },
  { id: 'parents', text: 'Parents', path: '/admin/parents' },
  { id: 'students', text: 'Students', path: '/admin/students' },
  { id: 'contracts', text: 'One-To-One', path: '/admin/contracts' },
  { id: 'group-classes', text: 'Group', path: '/admin/group-classes' },
  { id: 'attendance', text: 'Attendance', path: '/admin/attendance' },
  { id: 'payments', text: 'Payments', path: '/admin/payments' },
  { id: 'reports', text: 'Reports', path: '/admin/reports' },
  { id: 'settings', text: 'Settings', path: '/admin/settings' }
];

@Component({
  selector: 'app-admin-layout',
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.css']
})
export class AdminLayoutComponent {
  constructor(public auth: AuthService, private router: Router) {}

  get navItems(): NavItem[] {
    const url = this.router.url;
    return NAV_LINKS.map(n => ({
      ...n,
      selected: n.path === url || (n.path !== '/admin' && url.startsWith(n.path))
    }));
  }

  onNavItemClick(e: { itemData?: NavItem }): void {
    const item = e?.itemData;
    if (item && (item as NavItem).path) this.router.navigateByUrl((item as NavItem).path);
  }
}
