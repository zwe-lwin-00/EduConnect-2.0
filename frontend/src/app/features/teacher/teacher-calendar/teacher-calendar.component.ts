import { Component, OnInit } from '@angular/core';
import { TeacherApiService, CalendarDayItemDto, TeacherCalendarResponseDto } from '../../../../core/services/teacher-api.service';

interface DayCell {
  dateYmd: string;
  dayOfMonth: number;
  isCurrentMonth: boolean;
  isHoliday: boolean;
  items: CalendarDayItemDto[];
}

@Component({
  selector: 'app-teacher-calendar',
  templateUrl: './teacher-calendar.component.html',
  styleUrls: ['./teacher-calendar.component.css']
})
export class TeacherCalendarComponent implements OnInit {
  year = new Date().getFullYear();
  month = new Date().getMonth() + 1;
  loading = true;
  error = '';
  items: CalendarDayItemDto[] = [];
  holidays: string[] = [];
  /** 7 columns (Sun–Sat), multiple rows. First row may start with empty cells. */
  weeks: DayCell[][] = [];
  weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getCalendar(this.year, this.month).subscribe({
      next: (res: TeacherCalendarResponseDto) => {
        this.items = res.items || [];
        this.holidays = res.holidays || [];
        this.buildGrid();
        this.loading = false;
      },
      error: () => { this.loading = false; this.error = 'Failed to load calendar. Please try again.'; }
    });
  }

  prevMonth(): void {
    if (this.month === 1) {
      this.month = 12;
      this.year--;
    } else {
      this.month--;
    }
    this.load();
  }

  nextMonth(): void {
    if (this.month === 12) {
      this.month = 1;
      this.year++;
    } else {
      this.month++;
    }
    this.load();
  }

  monthLabel(): string {
    const d = new Date(this.year, this.month - 1, 1);
    return d.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });
  }

  private buildGrid(): void {
    const first = new Date(this.year, this.month - 1, 1);
    const last = new Date(this.year, this.month, 0);
    const startDay = first.getDay();
    const daysInMonth = last.getDate();
    const holidaySet = new Set(this.holidays);
    const itemsByDate = new Map<string, CalendarDayItemDto[]>();
    for (const it of this.items) {
      const list = itemsByDate.get(it.dateYmd) || [];
      list.push(it);
      itemsByDate.set(it.dateYmd, list);
    }

    const cells: DayCell[] = [];
    const pad = startDay;
    for (let i = 0; i < pad; i++) {
      const prevMonthDate = new Date(this.year, this.month - 1, 1 - (pad - i));
      const ymd = this.toYmd(prevMonthDate);
      cells.push({
        dateYmd: ymd,
        dayOfMonth: prevMonthDate.getDate(),
        isCurrentMonth: false,
        isHoliday: holidaySet.has(ymd),
        items: itemsByDate.get(ymd) || []
      });
    }
    for (let d = 1; d <= daysInMonth; d++) {
      const ymd = `${this.year}-${String(this.month).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
      cells.push({
        dateYmd: ymd,
        dayOfMonth: d,
        isCurrentMonth: true,
        isHoliday: holidaySet.has(ymd),
        items: itemsByDate.get(ymd) || []
      });
    }
    const remainder = cells.length % 7;
    const nextPad = remainder === 0 ? 0 : 7 - remainder;
    const nextMonthStart = new Date(this.year, this.month, 1);
    for (let i = 0; i < nextPad; i++) {
      const nextDate = new Date(nextMonthStart);
      nextDate.setDate(nextDate.getDate() + i);
      const ymd = this.toYmd(nextDate);
      cells.push({
        dateYmd: ymd,
        dayOfMonth: nextDate.getDate(),
        isCurrentMonth: false,
        isHoliday: holidaySet.has(ymd),
        items: itemsByDate.get(ymd) || []
      });
    }
    this.weeks = [];
    for (let r = 0; r < cells.length; r += 7) {
      this.weeks.push(cells.slice(r, r + 7));
    }
  }

  private toYmd(d: Date): string {
    const y = d.getFullYear();
    const m = d.getMonth() + 1;
    const day = d.getDate();
    return `${y}-${String(m).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
  }
}
