import { Component, OnInit } from '@angular/core';
import { AdminApiService, SubscriptionDto, StudentDto } from '../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-payments',
  templateUrl: './admin-payments.component.html',
  styleUrls: ['./admin-payments.component.css']
})
export class AdminPaymentsComponent implements OnInit {
  subscriptions: SubscriptionDto[] = [];
  students: StudentDto[] = [];
  loading = true;
  error = '';
  showCreate = false;
  createForm: { studentId: string; type: string; startDate: string | Date | number } = { studentId: '', type: 'ONE_TO_ONE', startDate: '' };
  typeOptions = [{ value: 'ONE_TO_ONE', label: 'One-To-One' }, { value: 'GROUP', label: 'Group' }];

  get studentOptions(): { id: string; label: string }[] {
    return this.students.map(s => ({ id: s.id, label: `${s.fullName} (${s.grade})` }));
  }

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getStudents().subscribe(list => this.students = list);
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getSubscriptions().subscribe({
      next: list => { this.subscriptions = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load subscriptions. Please try again.'; }
    });
  }

  closeCreate(): void {
    this.showCreate = false;
  }

  openCreate(): void {
    const today = new Date().toISOString().slice(0, 10);
    this.createForm = { studentId: '', type: 'ONE_TO_ONE', startDate: today };
    this.showCreate = true;
  }

  submitCreate(): void {
    const startVal = this.createForm.startDate;
    let start: string;
    if (typeof startVal === 'string') start = startVal;
    else if (typeof startVal === 'number') start = new Date(startVal).toISOString().slice(0, 10);
    else start = (startVal as Date).toISOString().slice(0, 10);
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
