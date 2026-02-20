import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getApiUrl } from './api-url';

const BASE = getApiUrl('teacher');

export interface CalendarDayItemDto {
  dateYmd: string;
  type: string;
  label: string;
  completed: boolean;
  id?: string;
}

export interface TeacherCalendarResponseDto {
  items: CalendarDayItemDto[];
  holidays: string[];
}

export interface TeacherDashboardData {
  todayOneToOneSessions: number;
  todayGroupSessions: number;
  upcomingOneToOneCount: number;
  pendingHomeworkToGrade: number;
}

export interface AvailabilitySlotDto {
  id: string;
  dayOfWeek: number;
  startTime: string;
  endTime: string;
}

export interface TeacherStudentDto {
  id: string;
  fullName: string;
  grade: string;
}

export interface TeacherSessionDto {
  id: string;
  type: string;
  sessionDate: string;
  studentName?: string;
  studentNames?: string[];
  groupClassName?: string;
  contractId?: string;
  groupSessionId?: string;
  groupClassId?: string;
  checkInAt?: string;
  checkOutAt?: string;
  hoursUsed?: number;
  lessonNotes?: string;
  zoomJoinUrl?: string;
}

export interface TeacherGroupClassDto {
  id: string;
  name: string;
  zoomJoinUrl?: string;
  active: boolean;
  daysOfWeek?: number[];
  scheduleStartTime?: string;
  scheduleEndTime?: string;
  enrollmentCount: number;
}

export interface TeacherHomeworkDto {
  id: string;
  studentId: string;
  studentName: string;
  title: string;
  description?: string;
  dueDate: string;
  status: string;
  teacherFeedback?: string;
}

export interface TeacherGradeDto {
  id: string;
  studentId: string;
  studentName: string;
  title: string;
  gradeValue: number;
  maxValue?: number;
  gradeDate: string;
  notes?: string;
}

export interface TeacherProfileDto {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  education?: string;
  bio?: string;
  specializations?: string[];
  verificationStatus: string;
  zoomJoinUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class TeacherApiService {
  constructor(private http: HttpClient) {}

  getDashboard(): Observable<TeacherDashboardData> {
    return this.http.get<TeacherDashboardData>(`${BASE}/dashboard`);
  }

  getAvailability(): Observable<AvailabilitySlotDto[]> {
    return this.http.get<AvailabilitySlotDto[]>(`${BASE}/availability`);
  }
  setAvailability(slots: AvailabilitySlotDto[]): Observable<AvailabilitySlotDto[]> {
    return this.http.put<AvailabilitySlotDto[]>(`${BASE}/availability`, slots);
  }

  getStudents(): Observable<TeacherStudentDto[]> {
    return this.http.get<TeacherStudentDto[]>(`${BASE}/students`);
  }

  getContracts(): Observable<{ id: string; studentId: string; studentName: string }[]> {
    return this.http.get<{ id: string; studentId: string; studentName: string }[]>(`${BASE}/contracts`);
  }

  getSessions(from?: string, to?: string): Observable<TeacherSessionDto[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<TeacherSessionDto[]>(`${BASE}/sessions`, { params });
  }
  createAttendance(contractId: string, sessionDate?: string): Observable<TeacherSessionDto> {
    let params = new HttpParams().set('contractId', contractId);
    if (sessionDate) params = params.set('sessionDate', sessionDate);
    return this.http.post<TeacherSessionDto>(`${BASE}/sessions/attendance`, {}, { params });
  }
  updateAttendance(id: string, patch: { checkInAt?: string; checkOutAt?: string; hoursUsed?: number; lessonNotes?: string }): Observable<TeacherSessionDto> {
    let params = new HttpParams();
    if (patch.checkInAt != null) params = params.set('checkInAt', patch.checkInAt);
    if (patch.checkOutAt != null) params = params.set('checkOutAt', patch.checkOutAt);
    if (patch.hoursUsed != null) params = params.set('hoursUsed', String(patch.hoursUsed));
    if (patch.lessonNotes != null) params = params.set('lessonNotes', patch.lessonNotes);
    return this.http.patch<TeacherSessionDto>(`${BASE}/sessions/attendance/${id}`, {}, { params });
  }
  updateGroupSession(id: string, patch: { checkInAt?: string; checkOutAt?: string; lessonNotes?: string }): Observable<TeacherSessionDto> {
    let params = new HttpParams();
    if (patch.checkInAt != null) params = params.set('checkInAt', patch.checkInAt);
    if (patch.checkOutAt != null) params = params.set('checkOutAt', patch.checkOutAt);
    if (patch.lessonNotes != null) params = params.set('lessonNotes', patch.lessonNotes);
    return this.http.patch<TeacherSessionDto>(`${BASE}/sessions/group/${id}`, {}, { params });
  }

  getGroupClasses(): Observable<TeacherGroupClassDto[]> {
    return this.http.get<TeacherGroupClassDto[]>(`${BASE}/group-classes`);
  }
  updateGroupClass(id: string, body: Partial<{ name: string; zoomJoinUrl: string; active: boolean }>): Observable<TeacherGroupClassDto> {
    return this.http.patch<TeacherGroupClassDto>(`${BASE}/group-classes/${id}`, body);
  }
  addEnrollment(groupClassId: string, studentId: string, subscriptionId?: string, contractId?: string): Observable<void> {
    let params = new HttpParams().set('studentId', studentId);
    if (subscriptionId) params = params.set('subscriptionId', subscriptionId);
    if (contractId) params = params.set('contractId', contractId);
    return this.http.post<void>(`${BASE}/group-classes/${groupClassId}/enrollments`, {}, { params });
  }
  removeEnrollment(groupClassId: string, studentId: string): Observable<void> {
    return this.http.delete<void>(`${BASE}/group-classes/${groupClassId}/enrollments/${studentId}`);
  }

  getHomework(studentId?: string): Observable<TeacherHomeworkDto[]> {
    const params = studentId ? new HttpParams().set('studentId', studentId) : undefined;
    return this.http.get<TeacherHomeworkDto[]>(`${BASE}/homework`, { params });
  }
  createHomework(body: { studentId: string; title: string; description?: string; dueDate: string }): Observable<TeacherHomeworkDto> {
    return this.http.post<TeacherHomeworkDto>(`${BASE}/homework`, body);
  }
  updateHomework(id: string, body: { status?: string; teacherFeedback?: string }): Observable<TeacherHomeworkDto> {
    return this.http.patch<TeacherHomeworkDto>(`${BASE}/homework/${id}`, body);
  }

  getGrades(studentId?: string): Observable<TeacherGradeDto[]> {
    const params = studentId ? new HttpParams().set('studentId', studentId) : undefined;
    return this.http.get<TeacherGradeDto[]>(`${BASE}/grades`, { params });
  }
  createGrade(body: { studentId: string; title: string; gradeValue: number; maxValue?: number; gradeDate?: string; notes?: string }): Observable<TeacherGradeDto> {
    return this.http.post<TeacherGradeDto>(`${BASE}/grades`, body);
  }

  getProfile(): Observable<TeacherProfileDto> {
    return this.http.get<TeacherProfileDto>(`${BASE}/profile`);
  }
  updateProfile(body: { zoomJoinUrl?: string }): Observable<TeacherProfileDto> {
    return this.http.patch<TeacherProfileDto>(`${BASE}/profile`, body);
  }

  getCalendar(year: number, month: number): Observable<TeacherCalendarResponseDto> {
    const params = new HttpParams().set('year', String(year)).set('month', String(month));
    return this.http.get<TeacherCalendarResponseDto>(`${BASE}/calendar`, { params });
  }
}
