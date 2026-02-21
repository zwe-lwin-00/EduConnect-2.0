import { Component, OnInit } from '@angular/core';
import { TeacherApiService, AvailabilitySlotDto } from '../../../core/services/teacher-api.service';

const DAY_OPTIONS = [
  { value: 1, label: 'Mon' }, { value: 2, label: 'Tue' }, { value: 3, label: 'Wed' },
  { value: 4, label: 'Thu' }, { value: 5, label: 'Fri' }, { value: 6, label: 'Sat' }, { value: 7, label: 'Sun' }
];

@Component({
  selector: 'app-teacher-availability',
  templateUrl: './teacher-availability.component.html',
  styleUrls: ['./teacher-availability.component.css']
})
export class TeacherAvailabilityComponent implements OnInit {
  slots: AvailabilitySlotDto[] = [];
  loading = true;
  error = '';
  saving = false;
  dayOptions = DAY_OPTIONS;
  newSlot = { dayOfWeek: 1, startTime: '09:00', endTime: '17:00' };

  constructor(public api: TeacherApiService) {}

  dayLabel(d: number): string { return this.dayOptions.find(o => o.value === d)?.label ?? ''; }

  ngOnInit(): void {
    this.api.getAvailability().subscribe({
      next: (list) => { this.slots = list; this.loading = false; this.error = ''; },
      error: () => { this.loading = false; this.error = 'Failed to load availability. Please try again.'; }
    });
  }

  addSlot(): void {
    this.slots = [...this.slots, {
      id: '',
      dayOfWeek: this.newSlot.dayOfWeek,
      startTime: this.newSlot.startTime,
      endTime: this.newSlot.endTime
    }];
  }

  removeSlot(i: number): void {
    this.slots = this.slots.filter((_, idx) => idx !== i);
  }

  toTimeStr(v: string | Date | number | null | undefined): string {
    if (v == null || v === '') return '';
    if (typeof v === 'string') return v;
    const d = typeof v === 'number' ? new Date(v) : (v as Date);
    return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
  }

  save(): void {
    const payload = this.slots.map(s => ({
      id: s.id,
      dayOfWeek: s.dayOfWeek,
      startTime: this.toTimeStr(s.startTime),
      endTime: this.toTimeStr(s.endTime)
    }));
    this.saving = true;
    this.api.setAvailability(payload).subscribe({
      next: (list) => { this.slots = list; this.saving = false; },
      error: () => this.saving = false
    });
  }
}
