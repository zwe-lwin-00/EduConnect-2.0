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
  date: string | Date | number = '';
  showOverride = false;
  selected: AttendanceDto | null = null;
  overrideForm: { checkInAt: Date | string | number | undefined; checkOutAt: Date | string | number | undefined; hoursUsed: number | undefined; lessonNotes: string } = { checkInAt: undefined, checkOutAt: undefined, hoursUsed: undefined, lessonNotes: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    let dateStr: string | undefined;
    if (!this.date) dateStr = undefined;
    else if (this.date instanceof Date) dateStr = this.date.toISOString().slice(0, 10);
    else if (typeof this.date === 'number') dateStr = new Date(this.date).toISOString().slice(0, 10);
    else dateStr = String(this.date);
    this.api.getAttendance(dateStr).subscribe({
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
      checkInAt: row.checkInAt ? new Date(row.checkInAt) : undefined,
      checkOutAt: row.checkOutAt ? new Date(row.checkOutAt) : undefined,
      hoursUsed: row.hoursUsed ?? undefined,
      lessonNotes: row.lessonNotes ?? ''
    };
    this.showOverride = true;
  }

  toISO(v: Date | string | number | undefined): string | undefined {
    if (v == null || v === '') return undefined;
    if (v instanceof Date) return v.toISOString();
    if (typeof v === 'number') return new Date(v).toISOString();
    return String(v);
  }

  saveOverride(): void {
    if (!this.selected) return;
    const patch: { checkInAt?: string; checkOutAt?: string; hoursUsed?: number; lessonNotes?: string } = {};
    const cIn = this.toISO(this.overrideForm.checkInAt);
    const cOut = this.toISO(this.overrideForm.checkOutAt);
    if (cIn) patch.checkInAt = cIn;
    if (cOut) patch.checkOutAt = cOut;
    if (this.overrideForm.hoursUsed != null) patch.hoursUsed = Number(this.overrideForm.hoursUsed);
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

