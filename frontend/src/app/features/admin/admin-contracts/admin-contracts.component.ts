import { Component, OnInit } from '@angular/core';
import { AdminApiService, ContractDto, TeacherDto, StudentDto, SubscriptionDto } from '../../../core/services/admin-api.service';

const DAY_LABELS: { value: number; label: string }[] = [
  { value: 1, label: 'Mon' }, { value: 2, label: 'Tue' }, { value: 3, label: 'Wed' },
  { value: 4, label: 'Thu' }, { value: 5, label: 'Fri' }, { value: 6, label: 'Sat' }, { value: 7, label: 'Sun' }
];

@Component({
  selector: 'app-admin-contracts',
  templateUrl: './admin-contracts.component.html',
  styleUrls: ['./admin-contracts.component.css']
})
export class AdminContractsComponent implements OnInit {
  contracts: ContractDto[] = [];
  teachers: TeacherDto[] = [];
  students: StudentDto[] = [];
  subscriptions: SubscriptionDto[] = [];
  loading = true;
  error = '';
  showCreate = false;
  dayOptions = DAY_LABELS;
  form: any = { teacherId: '', studentId: '', subscriptionId: '', legacyPeriodEnd: '', daysOfWeek: [] as number[], scheduleStartTime: '', scheduleEndTime: '' };

  constructor(public api: AdminApiService) {}

  isDayChecked(dayValue: number): boolean {
    return Array.isArray(this.form.daysOfWeek) && this.form.daysOfWeek.includes(dayValue);
  }

  toggleDay(dayValue: number): void {
    const arr = Array.isArray(this.form.daysOfWeek) ? [...this.form.daysOfWeek] : [];
    const i = arr.indexOf(dayValue);
    if (i >= 0) arr.splice(i, 1);
    else arr.push(dayValue);
    arr.sort((a, b) => a - b);
    this.form.daysOfWeek = arr;
  }

  /** Schedule column text: e.g. "Mon, Wed · 09:00–10:00" */
  formatScheduleCell = (e: { data: ContractDto }): string => this.formatSchedule(e.data);

  formatSchedule(c: ContractDto): string {
    const days = (c.daysOfWeek && c.daysOfWeek.length) ? c.daysOfWeek.slice().sort((a, b) => a - b).map(d => DAY_LABELS[d - 1]?.label || String(d)).join(', ') : '';
    const start = c.scheduleStartTime ? String(c.scheduleStartTime).slice(0, 5) : '';
    const end = c.scheduleEndTime ? String(c.scheduleEndTime).slice(0, 5) : '';
    const time = (start && end) ? ` · ${start}–${end}` : (start || end) ? ` · ${start || end}` : '';
    if (!days) return time ? time.replace(/^ · /, '') : '—';
    return days + time;
  }

  ngOnInit(): void {
    this.load();
    this.api.getTeachers().subscribe(t => this.teachers = t);
    this.api.getStudents().subscribe(s => this.students = s);
    this.api.getSubscriptions(undefined, 'ONE_TO_ONE', 'ACTIVE').subscribe(s => this.subscriptions = s);
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getContracts().subscribe({
      next: list => { this.contracts = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load contracts. Please try again.'; }
    });
  }

  closeCreate(): void {
    this.showCreate = false;
  }

  openCreate(): void {
    this.showCreate = true;
    this.form = { teacherId: '', studentId: '', subscriptionId: '', legacyPeriodEnd: '', daysOfWeek: [], scheduleStartTime: '', scheduleEndTime: '' };
  }

  submitCreate(): void {
    const body: any = { teacherId: this.form.teacherId, studentId: this.form.studentId };
    if (this.form.subscriptionId) body.subscriptionId = this.form.subscriptionId;
    else if (this.form.legacyPeriodEnd) body.legacyPeriodEnd = this.form.legacyPeriodEnd;
    if (this.form.scheduleStartTime) body.scheduleStartTime = this.form.scheduleStartTime;
    if (this.form.scheduleEndTime) body.scheduleEndTime = this.form.scheduleEndTime;
    if (this.form.daysOfWeek?.length) body.daysOfWeek = this.form.daysOfWeek;
    this.api.createContract(body).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }

  cancelContract(c: ContractDto): void {
    if (confirm('Cancel this One-To-One contract?')) this.api.cancelContract(c.id).subscribe(() => this.load());
  }
}
