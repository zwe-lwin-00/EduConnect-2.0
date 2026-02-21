import { Component } from '@angular/core';
import { Router } from '@angular/router';

export interface TeacherNavItem {
  id: string;
  text: string;
  path: string;
  selected?: boolean;
}

const TEACHER_NAV_LINKS: Omit<TeacherNavItem, 'selected'>[] = [
  { id: 'dashboard', text: 'Dashboard', path: '/teacher' },
  { id: 'availability', text: 'Availability', path: '/teacher/availability' },
  { id: 'students', text: 'Students', path: '/teacher/students' },
  { id: 'sessions', text: 'Sessions', path: '/teacher/sessions' },
  { id: 'calendar', text: 'Calendar', path: '/teacher/calendar' },
  { id: 'group-classes', text: 'Group classes', path: '/teacher/group-classes' },
  { id: 'homework-grades', text: 'Homework & Grades', path: '/teacher/homework-grades' },
  { id: 'profile', text: 'Profile', path: '/teacher/profile' }
];

@Component({
  selector: 'app-teacher-layout',
  templateUrl: './teacher-layout.component.html',
  styleUrls: ['./teacher-layout.component.css']
})
export class TeacherLayoutComponent {
  constructor(private router: Router) {}

  get navItems(): TeacherNavItem[] {
    const url = this.router.url;
    return TEACHER_NAV_LINKS.map(n => ({
      ...n,
      selected: n.path === url || (n.path !== '/teacher' && url.startsWith(n.path))
    }));
  }

  onNavItemClick(e: { itemData?: TeacherNavItem }): void {
    const item = e?.itemData;
    if (item && (item as TeacherNavItem).path) this.router.navigateByUrl((item as TeacherNavItem).path);
  }
}
