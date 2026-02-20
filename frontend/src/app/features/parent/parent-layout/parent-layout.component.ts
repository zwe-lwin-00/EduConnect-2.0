import { Component } from '@angular/core';

@Component({
  selector: 'app-parent-layout',
  template: `
    <div class="parent-layout">
      <header class="header">
        <app-brand subtitle="Parent" class="header-brand"></app-brand>
        <app-logout-button></app-logout-button>
      </header>
      <main class="content"><router-outlet></router-outlet></main>
    </div>
  `,
  styles: [
    '.parent-layout { min-height: 100vh; }',
    '.header { padding: 1rem 1.5rem; background: #0f3460; color: #fff; display: flex; justify-content: space-between; align-items: center; }',
    '.header-brand { margin: 0; color: #fff; }',
    '.header .subtitle { color: #94a3b8; }',
    '.content { padding: 1.5rem; }'
  ]
})
export class ParentLayoutComponent {}
