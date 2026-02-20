import { Component, OnInit } from '@angular/core';
import { AdminApiService, AttendanceDto } from '../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-attendance',
  templateUrl: './admin-attendance.component.html',
  styleUrls: ['./admin-attendance.component.css']
})
export class AdminAttendanceComponent implements OnInit {
  list: AttendanceDto[] = [];
  loading = true;
  error = '';
  date = '';
  showOverride = false;
  selected: AttendanceDto | null = null;
  overrideForm = { checkInAt: '', checkOutAt: '', hoursUsed: '' as string | number, lessonNotes: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getAttendance(this.date || undefined).subscribe({
      next: list => { this.list = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load attendance. Please try again.'; }
    });
  }

  onDateChange(): void {
    this.load();
  }

  openOverride(row: AttendanceDto): void {
    this.selected = row;
    this.overrideForm = {
      checkInAt: row.checkInAt ? toDatetimeLocal(row.checkInAt) : '',
      checkOutAt: row.checkOutAt ? toDatetimeLocal(row.checkOutAt) : '',
      hoursUsed: row.hoursUsed ?? '',
      lessonNotes: row.lessonNotes ?? ''
    };
    this.showOverride = true;
  }

  saveOverride(): void {
    if (!this.selected) return;
    const patch: { checkInAt?: string; checkOutAt?: string; hoursUsed?: number; lessonNotes?: string } = {};
    if (this.overrideForm.checkInAt) patch.checkInAt = toISO(this.overrideForm.checkInAt);
    if (this.overrideForm.checkOutAt) patch.checkOutAt = toISO(this.overrideForm.checkOutAt);
    if (this.overrideForm.hoursUsed !== '' && this.overrideForm.hoursUsed !== null) {
      patch.hoursUsed = Number(this.overrideForm.hoursUsed);
    }
    if (this.overrideForm.lessonNotes != null) patch.lessonNotes = this.overrideForm.lessonNotes;
    this.api.overrideAttendance(this.selected.id, patch).subscribe({
      next: () => { this.load(); this.showOverride = false; this.selected = null; },
      error: () => {}
    });
  }

  closeOverride(): void {
    this.showOverride = false;
    this.selected = null;
  }
}

function toDatetimeLocal(iso: string): string {
  try {
    const d = new Date(iso);
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const h = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${y}-${m}-${day}T${h}:${min}`;
  } catch {
    return '';
  }
}

function toISO(datetimeLocal: string): string {
  return new Date(datetimeLocal).toISOString();
}
