import { Component, OnInit } from '@angular/core';
import { AdminApiService, ReportDto } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-reports',
  templateUrl: './admin-reports.component.html',
  styleUrls: ['./admin-reports.component.css']
})
export class AdminReportsComponent implements OnInit {
  daily: ReportDto[] = [];
  monthly: ReportDto[] = [];
  loading = true;
  from = '';
  to = '';

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    const today = new Date();
    this.to = today.toISOString().slice(0, 10);
    const weekAgo = new Date(today);
    weekAgo.setDate(weekAgo.getDate() - 7);
    this.from = weekAgo.toISOString().slice(0, 10);
    this.loadDaily();
    this.loadMonthly();
  }

  loadDaily(): void {
    if (!this.from || !this.to) return;
    this.loading = true;
    this.api.getDailyReport(this.from, this.to).subscribe({
      next: list => { this.daily = list; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadMonthly(): void {
    this.api.getMonthlyReport(this.from || undefined, this.to || undefined).subscribe(list => (this.monthly = list));
  }
}
