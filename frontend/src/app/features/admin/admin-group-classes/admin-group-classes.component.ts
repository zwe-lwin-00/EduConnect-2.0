import { Component, OnInit } from '@angular/core';
import { AdminApiService, GroupClassDto, TeacherDto } from '../../../../core/services/admin-api.service';

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
  form: any = { name: '', teacherId: '', scheduleStartTime: '', scheduleEndTime: '', daysOfWeek: [] as number[] };

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
    this.form = { name: '', teacherId: '', scheduleStartTime: '', scheduleEndTime: '', daysOfWeek: [] };
  }

  submitCreate(): void {
    const body: any = { name: this.form.name, teacherId: this.form.teacherId };
    if (this.form.scheduleStartTime) body.scheduleStartTime = this.form.scheduleStartTime;
    if (this.form.scheduleEndTime) body.scheduleEndTime = this.form.scheduleEndTime;
    if (this.form.daysOfWeek?.length) body.daysOfWeek = this.form.daysOfWeek;
    this.api.createGroupClass(body).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }
}
