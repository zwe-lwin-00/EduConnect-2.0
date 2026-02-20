import { Component } from '@angular/core';

@Component({
  selector: 'app-teacher-layout',
  template: `
    <div class="teacher-layout">
      <aside class="sidebar" aria-label="Teacher navigation">
        <app-brand subtitle="Teacher" class="sidebar-brand"></app-brand>
        <nav aria-label="Main">
          <a routerLink="/teacher" routerLinkActive="active" [routerLinkActiveOptions]="{ exact: true }">Dashboard</a>
          <a routerLink="/teacher/availability" routerLinkActive="active">Availability</a>
          <a routerLink="/teacher/students" routerLinkActive="active">Students</a>
          <a routerLink="/teacher/sessions" routerLinkActive="active">Sessions</a>
          <a routerLink="/teacher/calendar" routerLinkActive="active">Calendar</a>
          <a routerLink="/teacher/group-classes" routerLinkActive="active">Group classes</a>
          <a routerLink="/teacher/homework-grades" routerLinkActive="active">Homework & Grades</a>
          <a routerLink="/teacher/profile" routerLinkActive="active">Profile</a>
        </nav>
        <div class="user" role="region" aria-label="User menu">
          <app-notification-bell></app-notification-bell>
          <app-logout-button></app-logout-button>
        </div>
      </aside>
      <main class="content" aria-label="Main content"><router-outlet></router-outlet></main>
    </div>
  `,
  styles: [
    '.teacher-layout { display: flex; min-height: 100vh; }',
    '.sidebar { width: 200px; background: #1e3a5f; color: #e2e8f0; padding: 1rem; }',
    '.sidebar-brand { padding: 0 0 0.5rem 0; margin-bottom: 0.5rem; border-bottom: 1px solid #334155; color: #e2e8f0; }',
    '.sidebar a { color: #94a3b8; text-decoration: none; display: block; padding: 0.5rem 0; }',
    '.sidebar a.active { color: #fff; }',
    '.content { flex: 1; padding: 1.5rem; background: #f8fafc; }',
    '.user { margin-top: 1rem; display: flex; align-items: center; gap: 0.5rem; }'
  ]
})
export class TeacherLayoutComponent {}
