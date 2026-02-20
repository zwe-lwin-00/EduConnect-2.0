import { Component, OnInit } from '@angular/core';
import { AdminApiService, SubscriptionDto } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-payments',
  templateUrl: './admin-payments.component.html',
  styleUrls: ['./admin-payments.component.css']
})
export class AdminPaymentsComponent implements OnInit {
  subscriptions: SubscriptionDto[] = [];
  loading = true;

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getSubscriptions().subscribe({
      next: list => { this.subscriptions = list; this.loading = false; },
      error: () => this.loading = false
    });
  }

  renew(sub: SubscriptionDto, months: number): void {
    this.api.renewSubscription(sub.id, months).subscribe(() => this.load());
  }
}
