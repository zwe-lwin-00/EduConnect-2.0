import { Component, Input } from '@angular/core';

/**
 * Reusable primary action button with optional loading state.
 */
@Component({
  selector: 'app-primary-button',
  template: `
    <button type="submit" class="btn" [disabled]="disabled || loading" [class.loading]="loading">
      {{ loading ? (loadingLabel || 'Please wait…') : label }}
    </button>
  `,
  styleUrls: ['./primary-button.component.css']
})
export class PrimaryButtonComponent {
  @Input() label = 'Submit';
  @Input() loadingLabel = 'Please wait…';
  @Input() loading = false;
  @Input() disabled = false;
}
