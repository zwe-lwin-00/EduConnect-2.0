import { Component, OnInit } from '@angular/core';
import { AdminApiService, ReportDto } from '../../../../core/services/admin-api.service';

export interface ReportRow extends ReportDto {
  monthLabel?: string;
}

@Component({
  selector: 'app-admin-reports',
  templateUrl: './admin-reports.component.html',
  styleUrls: ['./admin-reports.component.css']
})
export class AdminReportsComponent implements OnInit {
  daily: ReportDto[] = [];
  monthly: ReportRow[] = [];
  loading = true;
  error = '';
  from = '';
  to = '';
  totalSessions = 0;
  totalRevenue = 0;

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
    this.error = '';
    this.api.getDailyReport(this.from, this.to).subscribe({
      next: list => {
        this.daily = list;
        this.totalSessions = list.reduce((s, r) => s + (r.sessionCount ?? 0), 0);
        this.totalRevenue = list.reduce((s, r) => s + (r.revenue ?? 0), 0);
        this.loading = false;
      },
      error: () => { this.loading = false; this.error = 'Failed to load reports. Please try again.'; }
    });
  }

  loadMonthly(): void {
    this.api.getMonthlyReport(this.from || undefined, this.to || undefined).subscribe({
      next: list => {
        this.monthly = list.map(r => ({
          ...r,
          monthLabel: this.monthLabel(r.date)
        }));
      }
    });
  }

  monthLabel(dateStr: string): string {
    if (!dateStr) return '';
    const d = new Date(dateStr + 'T12:00:00');
    return d.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  }
}
