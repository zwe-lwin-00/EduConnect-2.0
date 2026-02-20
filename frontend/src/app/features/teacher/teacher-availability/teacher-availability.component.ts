import { Component, OnInit } from '@angular/core';
import { TeacherApiService, AvailabilitySlotDto } from '../../../core/services/teacher-api.service';

const DAYS = ['', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

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
  newSlot = { dayOfWeek: 1, startTime: '09:00', endTime: '17:00' };

  constructor(public api: TeacherApiService) {}

  dayLabel(d: number): string { return DAYS[d] || ''; }

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

  save(): void {
    const payload = this.slots.map(s => ({
      id: s.id,
      dayOfWeek: s.dayOfWeek,
      startTime: s.startTime,
      endTime: s.endTime
    }));
    this.saving = true;
    this.api.setAvailability(payload).subscribe({
      next: (list) => { this.slots = list; this.saving = false; },
      error: () => this.saving = false
    });
  }
}
