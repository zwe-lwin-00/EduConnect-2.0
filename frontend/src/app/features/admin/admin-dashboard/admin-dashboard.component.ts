import { Component, OnInit } from '@angular/core';
import { AdminApiService, DashboardData } from '../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  data: Partial<DashboardData> = {};
  loading = true;
  error = '';
  summaryData: { metric: string; value: number | string }[] = [];

  constructor(private adminApi: AdminApiService) {}

  ngOnInit(): void {
    this.adminApi.getDashboard().subscribe({
      next: (res) => {
        this.data = res;
        this.summaryData = [
          { metric: 'Pending teacher verifications', value: res.pendingTeacherVerifications ?? 0 },
          { metric: "Today's sessions", value: res.todaySessionsCount ?? 0 },
          { metric: 'Subscriptions expiring soon', value: res.subscriptionsExpiringSoon ?? 0 },
          { metric: 'Contracts expiring soon', value: res.contractsExpiringSoon ?? 0 },
          { metric: 'Revenue this month', value: res.revenueThisMonth ?? 0 }
        ];
        this.loading = false;
        this.error = '';
      },
      error: () => {
        this.loading = false;
        this.error = 'Failed to load dashboard. Please try again.';
      }
    });
  }
}
