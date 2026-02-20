import { Component, Input } from '@angular/core';

/**
 * Reusable card wrapper for content (login card, dashboard cards, etc.).
 */
@Component({
  selector: 'app-card',
  template: `
    <div class="card" [class.padded]="padded">
      <ng-content></ng-content>
    </div>
  `,
  styleUrls: ['./card.component.css']
})
export class CardComponent {
  @Input() padded = true;
}
