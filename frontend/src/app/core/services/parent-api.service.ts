import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getApiUrl } from './api-url';

const BASE = getApiUrl('parent');

export interface ParentStudentDto {
  id: string;
  fullName: string;
  grade: string;
}

export interface SessionSummaryDto {
  sessionDate: string;
  type: string;
  teacherOrClassName: string;
  checkInAt?: string;
  checkOutAt?: string;
  lessonNotes?: string;
}

export interface HomeworkSummaryDto {
  id: string;
  title: string;
  dueDate: string;
  status: string;
  teacherFeedback?: string;
}

export interface GradeSummaryDto {
  id: string;
  title: string;
  gradeValue: number;
  maxValue?: number;
  gradeDate: string;
  notes?: string;
}

export interface StudentOverviewDto {
  studentId: string;
  studentName: string;
  grade: string;
  assignedTeacherName?: string;
  recentSessions: SessionSummaryDto[];
  homework: HomeworkSummaryDto[];
  grades: GradeSummaryDto[];
  totalSessionsCount: number;
  completedHomeworkCount: number;
}

@Injectable({ providedIn: 'root' })
export class ParentApiService {
  constructor(private http: HttpClient) {}

  getStudents(): Observable<ParentStudentDto[]> {
    return this.http.get<ParentStudentDto[]>(`${BASE}/students`);
  }

  getStudentOverview(studentId: string): Observable<StudentOverviewDto> {
    return this.http.get<StudentOverviewDto>(`${BASE}/students/${studentId}/overview`);
  }

  /** Month calendar for one student (items + holidays, DateYmd). */
  getStudentCalendar(studentId: string, year: number, month: number): Observable<{ items: { dateYmd: string; type: string; label: string; completed: boolean; id?: string }[]; holidays: string[] }> {
    const params = new HttpParams().set('year', String(year)).set('month', String(month));
    return this.http.get<{ items: { dateYmd: string; type: string; label: string; completed: boolean; id?: string }[]; holidays: string[] }>(
      `${BASE}/students/${studentId}/calendar`,
      { params }
    );
  }
}
