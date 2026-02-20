import { Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/**
 * Reusable placeholder for sections not yet implemented.
 * Title can be set via route data (title) or @Input().
 */
@Component({
  selector: 'app-placeholder',
  templateUrl: './placeholder.component.html',
  styleUrls: ['./placeholder.component.css']
})
export class PlaceholderComponent {
  @Input() title = 'Section';

  constructor(private route: ActivatedRoute) {
    const t = this.route.snapshot.data['title'];
    if (t) this.title = t;
  }
}
