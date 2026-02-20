import { Component, Input } from '@angular/core';

/**
 * Reusable app brand (EduConnect + optional subtitle).
 */
@Component({
  selector: 'app-brand',
  template: `
    <div class="brand">
      <span class="title">EduConnect</span>
      <span *ngIf="subtitle" class="subtitle">{{ subtitle }}</span>
    </div>
  `,
  styleUrls: ['./brand.component.css']
})
export class BrandComponent {
  @Input() subtitle = '';
}
