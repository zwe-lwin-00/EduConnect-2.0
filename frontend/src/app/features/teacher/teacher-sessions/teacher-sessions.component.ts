import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherSessionDto } from '../../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-sessions',
  templateUrl: './teacher-sessions.component.html',
  styleUrls: ['./teacher-sessions.component.css']
})
export class TeacherSessionsComponent implements OnInit {
  sessions: TeacherSessionDto[] = [];
  contracts: { id: string; studentId: string; studentName: string }[] = [];
  loading = true;
  error = '';
  from = '';
  to = '';
  showStart = false;
  startContractId = '';
  startDate = '';
  showNotes = false;
  selectedSession: TeacherSessionDto | null = null;
  lessonNotes = '';

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    const today = new Date().toISOString().slice(0, 10);
    this.from = today;
    const next = new Date();
    next.setDate(next.getDate() + 7);
    this.to = next.toISOString().slice(0, 10);
    this.load();
    this.api.getContracts().subscribe(list => this.contracts = list);
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getSessions(this.from || undefined, this.to || undefined).subscribe({
      next: (list) => { this.sessions = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load sessions. Please try again.'; }
    });
  }

  openStart(): void {
    this.startContractId = '';
    this.startDate = new Date().toISOString().slice(0, 10);
    this.showStart = true;
  }

  closeStart(): void {
    this.showStart = false;
  }

  closeNotes(): void {
    this.showNotes = false;
    this.selectedSession = null;
  }

  submitStart(): void {
    this.api.createAttendance(this.startContractId, this.startDate || undefined).subscribe({
      next: () => { this.load(); this.showStart = false; },
      error: () => {}
    });
  }

  checkIn(s: TeacherSessionDto): void {
    const now = new Date().toISOString();
    if (s.type === 'ONE_TO_ONE' && s.id) {
      this.api.updateAttendance(s.id, { checkInAt: now }).subscribe(() => this.load());
    } else if (s.type === 'GROUP' && s.groupSessionId) {
      this.api.updateGroupSession(s.groupSessionId, { checkInAt: now }).subscribe(() => this.load());
    }
  }

  checkOut(s: TeacherSessionDto): void {
    const now = new Date().toISOString();
    if (s.type === 'ONE_TO_ONE' && s.id) {
      this.api.updateAttendance(s.id, { checkOutAt: now }).subscribe(() => this.load());
    } else if (s.type === 'GROUP' && s.groupSessionId) {
      this.api.updateGroupSession(s.groupSessionId, { checkOutAt: now }).subscribe(() => this.load());
    }
  }

  openNotes(s: TeacherSessionDto): void {
    this.selectedSession = s;
    this.lessonNotes = s.lessonNotes || '';
    this.showNotes = true;
  }

  saveNotes(): void {
    if (!this.selectedSession) return;
    if (this.selectedSession.type === 'ONE_TO_ONE' && this.selectedSession.id) {
      this.api.updateAttendance(this.selectedSession.id, { lessonNotes: this.lessonNotes }).subscribe(() => { this.load(); this.showNotes = false; });
    } else if (this.selectedSession.type === 'GROUP' && this.selectedSession.groupSessionId) {
      this.api.updateGroupSession(this.selectedSession.groupSessionId, { lessonNotes: this.lessonNotes }).subscribe(() => { this.load(); this.showNotes = false; });
    }
  }

  studentDisplay(s: TeacherSessionDto): string {
    if (s.type === 'ONE_TO_ONE') return s.studentName || '';
    return s.studentNames?.join(', ') || s.groupClassName || '';
  }

  /** Zoom URL to show only when session is in progress (checked in, not checked out). */
  zoomJoinUrlWhenInProgress = (row: TeacherSessionDto): string | null => {
    if (!this.isSessionInProgress(row) || !row.zoomJoinUrl) return null;
    return row.zoomJoinUrl;
  };

  isSessionInProgress(s: TeacherSessionDto): boolean {
    return !!s.checkInAt && !s.checkOutAt;
  }
}
