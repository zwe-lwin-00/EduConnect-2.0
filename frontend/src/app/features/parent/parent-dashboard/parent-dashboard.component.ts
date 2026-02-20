import { Component } from '@angular/core';

@Component({
  selector: 'app-parent-dashboard',
  template: `
    <div class="parent-dashboard">
      <h1>My Students</h1>
      <p>View your linked students and their learning overview.</p>
    </div>
  `,
  styles: ['.parent-dashboard { padding: 2rem; }']
})
export class ParentDashboardComponent {}
