import { Component, OnInit } from '@angular/core';
import { AdminApiService, GroupClassDto, TeacherDto } from '../../../core/services/admin-api.service';

const DAY_LABELS: { value: number; label: string }[] = [
  { value: 1, label: 'Mon' }, { value: 2, label: 'Tue' }, { value: 3, label: 'Wed' },
  { value: 4, label: 'Thu' }, { value: 5, label: 'Fri' }, { value: 6, label: 'Sat' }, { value: 7, label: 'Sun' }
];

@Component({
  selector: 'app-admin-group-classes',
  templateUrl: './admin-group-classes.component.html',
  styleUrls: ['./admin-group-classes.component.css']
})
export class AdminGroupClassesComponent implements OnInit {
  list: GroupClassDto[] = [];
  teachers: TeacherDto[] = [];
  loading = true;
  error = '';
  showCreate = false;
  dayOptions = DAY_LABELS;
  form: any = { name: '', teacherId: '', scheduleStartTime: null as string | null, scheduleEndTime: null as string | null, daysOfWeek: [] as number[] };

  constructor(public api: AdminApiService) {}

  toTimeStr(v: string | Date | null): string {
    if (!v) return '';
    const d = typeof v === 'string' ? new Date('1970-01-01T' + v) : (v as Date);
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  }

  ngOnInit(): void {
    this.load();
    this.api.getTeachers().subscribe(t => this.teachers = t);
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getGroupClasses().subscribe({
      next: list => { this.list = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load group classes. Please try again.'; }
    });
  }

  closeCreate(): void {
    this.showCreate = false;
  }

  openCreate(): void {
    this.showCreate = true;
    this.form = { name: '', teacherId: '', scheduleStartTime: null, scheduleEndTime: null, daysOfWeek: [] };
  }

  submitCreate(): void {
    const body: any = { name: this.form.name, teacherId: this.form.teacherId };
    const start = this.toTimeStr(this.form.scheduleStartTime);
    const end = this.toTimeStr(this.form.scheduleEndTime);
    if (start) body.scheduleStartTime = start;
    if (end) body.scheduleEndTime = end;
    if (this.form.daysOfWeek?.length) body.daysOfWeek = this.form.daysOfWeek;
    this.api.createGroupClass(body).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }
}
