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
  showConfirmCancel = false;
  contractToCancel: ContractDto | null = null;
  dayOptions = DAY_LABELS;
  form: any = { teacherId: '', studentId: '', subscriptionId: '', legacyPeriodEnd: '' as string | Date, daysOfWeek: [] as number[], scheduleStartTime: null as string | null, scheduleEndTime: null as string | null };

  get subscriptionOptions(): { id: string; label: string }[] {
    return this.subscriptions.map(s => ({ id: s.id, label: `${s.studentName} until ${s.endDate}` }));
  }

  constructor(public api: AdminApiService) {}

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
    this.form = { teacherId: '', studentId: '', subscriptionId: '', legacyPeriodEnd: '', daysOfWeek: [], scheduleStartTime: null, scheduleEndTime: null };
  }

  toDateStr(v: string | Date | null): string {
    if (!v) return '';
    if (typeof v === 'string') return v;
    return (v as Date).toISOString().slice(0, 10);
  }
  toTimeStr(v: string | Date | null): string {
    if (!v) return '';
    const d = typeof v === 'string' ? new Date('1970-01-01T' + v) : (v as Date);
    const h = d.getHours();
    const m = d.getMinutes();
    return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
  }

  submitCreate(): void {
    const body: any = { teacherId: this.form.teacherId, studentId: this.form.studentId };
    if (this.form.subscriptionId) body.subscriptionId = this.form.subscriptionId;
    else {
      const end = this.toDateStr(this.form.legacyPeriodEnd);
      if (end) body.legacyPeriodEnd = end;
    }
    const startTime = this.toTimeStr(this.form.scheduleStartTime);
    const endTime = this.toTimeStr(this.form.scheduleEndTime);
    if (startTime) body.scheduleStartTime = startTime;
    if (endTime) body.scheduleEndTime = endTime;
    if (this.form.daysOfWeek?.length) body.daysOfWeek = this.form.daysOfWeek;
    this.api.createContract(body).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }

  cancelContract(c: ContractDto): void {
    this.contractToCancel = c;
    this.showConfirmCancel = true;
  }

  confirmCancelContract(): void {
    if (!this.contractToCancel) return;
    this.api.cancelContract(this.contractToCancel.id).subscribe(() => {
      this.load();
      this.showConfirmCancel = false;
      this.contractToCancel = null;
    });
  }
}
