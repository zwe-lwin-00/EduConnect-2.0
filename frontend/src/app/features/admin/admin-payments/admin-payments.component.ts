import { Component, OnInit } from '@angular/core';
import { AdminApiService, SubscriptionDto, StudentDto } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-payments',
  templateUrl: './admin-payments.component.html',
  styleUrls: ['./admin-payments.component.css']
})
export class AdminPaymentsComponent implements OnInit {
  subscriptions: SubscriptionDto[] = [];
  students: StudentDto[] = [];
  loading = true;
  showCreate = false;
  createForm = { studentId: '', type: 'ONE_TO_ONE' as string, startDate: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getStudents().subscribe(list => this.students = list);
  }

  load(): void {
    this.loading = true;
    this.api.getSubscriptions().subscribe({
      next: list => { this.subscriptions = list; this.loading = false; },
      error: () => this.loading = false
    });
  }

  openCreate(): void {
    const today = new Date().toISOString().slice(0, 10);
    this.createForm = { studentId: this.students[0]?.id || '', type: 'ONE_TO_ONE', startDate: today };
    this.showCreate = true;
  }

  submitCreate(): void {
    const start = this.createForm.startDate;
    const end = new Date(start);
    end.setMonth(end.getMonth() + 1);
    const endStr = end.toISOString().slice(0, 10);
    this.api.createSubscription({
      studentId: this.createForm.studentId,
      type: this.createForm.type,
      startDate: start,
      endDate: endStr
    }).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }

  renew(sub: SubscriptionDto, months: number): void {
    this.api.renewSubscription(sub.id, months).subscribe(() => this.load());
  }
}
