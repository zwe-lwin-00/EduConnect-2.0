import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

const BASE = `${environment.apiUrl}/admin`;

export interface DashboardData {
  pendingTeacherVerifications: number;
  todaySessionsCount: number;
  subscriptionsExpiringSoon: number;
  contractsExpiringSoon: number;
  revenueThisMonth?: number;
  pendingActions?: string[];
  alerts?: unknown[];
}

export interface TeacherDto {
  id: string;
  userId: string;
  email: string;
  fullName: string;
  phone?: string;
  education?: string;
  bio?: string;
  specializations?: string[];
  verificationStatus: string;
  hourlyRate?: number;
  active: boolean;
}

export interface ParentDto {
  id: string;
  email: string;
  fullName: string;
  phone?: string;
  active: boolean;
}

export interface CreateParentResponse {
  id: string;
  email: string;
  temporaryPassword: string;
}

export interface StudentDto {
  id: string;
  fullName: string;
  grade: string;
  dateOfBirth?: string;
  parentId: string;
  parentName?: string;
  active: boolean;
}

export interface ContractDto {
  id: string;
  teacherId: string;
  teacherName: string;
  studentId: string;
  studentName: string;
  subscriptionId?: string;
  legacyPeriodEnd?: string;
  daysOfWeek?: number[];
  scheduleStartTime?: string;
  scheduleEndTime?: string;
  status: string;
}

export interface GroupClassDto {
  id: string;
  name: string;
  teacherId: string;
  teacherName: string;
  active: boolean;
  daysOfWeek?: number[];
  scheduleStartTime?: string;
  scheduleEndTime?: string;
  enrollmentCount: number;
}

export interface SubscriptionDto {
  id: string;
  studentId: string;
  studentName: string;
  type: string;
  startDate: string;
  endDate: string;
  status: string;
}

export interface AttendanceDto {
  id: string;
  contractId: string;
  teacherName: string;
  studentName: string;
  sessionDate: string;
  checkInAt?: string;
  checkOutAt?: string;
  hoursUsed?: number;
  lessonNotes?: string;
}

export interface ReportDto {
  date: string;
  sessionCount: number;
  revenue: number;
}

export interface HolidayDto {
  id: string;
  holidayDate: string;
  name: string;
  description?: string;
}

export interface SystemSettingDto {
  id: string;
  keyName: string;
  value: string;
  description?: string;
}

@Injectable({ providedIn: 'root' })
export class AdminApiService {
  constructor(private http: HttpClient) {}

  getDashboard(date?: string): Observable<DashboardData> {
    const params = date ? new HttpParams().set('date', date) : undefined;
    return this.http.get<DashboardData>(`${BASE}/dashboard`, { params });
  }

  // Teachers
  getTeachers(verificationStatus?: string): Observable<TeacherDto[]> {
    const params = verificationStatus ? new HttpParams().set('verificationStatus', verificationStatus) : undefined;
    return this.http.get<TeacherDto[]>(`${BASE}/teachers`, { params });
  }
  getTeacher(id: string): Observable<TeacherDto> {
    return this.http.get<TeacherDto>(`${BASE}/teachers/${id}`);
  }
  onboardTeacher(body: Record<string, unknown>): Observable<{ teacher: TeacherDto; temporaryPassword: string }> {
    return this.http.post<{ teacher: TeacherDto; temporaryPassword: string }>(`${BASE}/teachers`, body);
  }
  updateTeacher(id: string, body: Record<string, unknown>): Observable<TeacherDto> {
    return this.http.patch<TeacherDto>(`${BASE}/teachers/${id}`, body);
  }
  verifyTeacher(id: string): Observable<TeacherDto> {
    return this.http.post<TeacherDto>(`${BASE}/teachers/${id}/verify`, {});
  }
  rejectTeacher(id: string): Observable<TeacherDto> {
    return this.http.post<TeacherDto>(`${BASE}/teachers/${id}/reject`, {});
  }
  activateTeacher(id: string): Observable<TeacherDto> {
    return this.http.post<TeacherDto>(`${BASE}/teachers/${id}/activate`, {});
  }
  suspendTeacher(id: string): Observable<TeacherDto> {
    return this.http.post<TeacherDto>(`${BASE}/teachers/${id}/suspend`, {});
  }
  resetTeacherPassword(id: string): Observable<{ temporaryPassword: string; email: string; message: string }> {
    return this.http.post<{ temporaryPassword: string; email: string; message: string }>(`${BASE}/teachers/${id}/reset-password`, {});
  }

  // Parents
  getParents(): Observable<ParentDto[]> {
    return this.http.get<ParentDto[]>(`${BASE}/parents`);
  }
  createParent(body: { email: string; fullName: string; phone?: string }): Observable<CreateParentResponse> {
    return this.http.post<CreateParentResponse>(`${BASE}/parents`, body);
  }

  // Students
  getStudents(parentId?: string): Observable<StudentDto[]> {
    const params = parentId ? new HttpParams().set('parentId', parentId) : undefined;
    return this.http.get<StudentDto[]>(`${BASE}/students`, { params });
  }
  createStudent(body: { fullName: string; grade: string; dateOfBirth?: string; parentId: string }): Observable<StudentDto> {
    return this.http.post<StudentDto>(`${BASE}/students`, body);
  }
  updateStudent(id: string, body: { active?: boolean }): Observable<StudentDto> {
    return this.http.patch<StudentDto>(`${BASE}/students/${id}`, body);
  }

  // Contracts (One-To-One)
  getContracts(status?: string): Observable<ContractDto[]> {
    const params = status ? new HttpParams().set('status', status) : undefined;
    return this.http.get<ContractDto[]>(`${BASE}/contracts`, { params });
  }
  createContract(body: Record<string, unknown>): Observable<ContractDto> {
    return this.http.post<ContractDto>(`${BASE}/contracts`, body);
  }
  cancelContract(id: string): Observable<ContractDto> {
    return this.http.post<ContractDto>(`${BASE}/contracts/${id}/cancel`, {});
  }

  // Group classes
  getGroupClasses(): Observable<GroupClassDto[]> {
    return this.http.get<GroupClassDto[]>(`${BASE}/group-classes`);
  }
  createGroupClass(body: Record<string, unknown>): Observable<GroupClassDto> {
    return this.http.post<GroupClassDto>(`${BASE}/group-classes`, body);
  }
  updateGroupClass(id: string, body: Record<string, unknown>): Observable<GroupClassDto> {
    return this.http.patch<GroupClassDto>(`${BASE}/group-classes/${id}`, body);
  }
  addEnrollment(groupClassId: string, studentId: string, subscriptionId?: string): Observable<void> {
    let params = new HttpParams().set('studentId', studentId);
    if (subscriptionId) params = params.set('subscriptionId', subscriptionId);
    return this.http.post<void>(`${BASE}/group-classes/${groupClassId}/enrollments`, {}, { params });
  }
  removeEnrollment(groupClassId: string, studentId: string): Observable<void> {
    return this.http.delete<void>(`${BASE}/group-classes/${groupClassId}/enrollments/${studentId}`);
  }

  // Attendance
  getAttendance(date?: string): Observable<AttendanceDto[]> {
    const params = date ? new HttpParams().set('date', date) : undefined;
    return this.http.get<AttendanceDto[]>(`${BASE}/attendance`, { params });
  }
  overrideAttendance(id: string, patch: { checkInAt?: string; checkOutAt?: string; hoursUsed?: number; lessonNotes?: string }): Observable<AttendanceDto> {
    let params = new HttpParams();
    if (patch.checkInAt) params = params.set('checkInAt', patch.checkInAt);
    if (patch.checkOutAt) params = params.set('checkOutAt', patch.checkOutAt);
    if (patch.hoursUsed != null) params = params.set('hoursUsed', String(patch.hoursUsed));
    if (patch.lessonNotes != null) params = params.set('lessonNotes', patch.lessonNotes);
    return this.http.patch<AttendanceDto>(`${BASE}/attendance/${id}`, {}, { params });
  }

  // Subscriptions
  getSubscriptions(studentId?: string, type?: string, status?: string): Observable<SubscriptionDto[]> {
    let params = new HttpParams();
    if (studentId) params = params.set('studentId', studentId);
    if (type) params = params.set('type', type);
    if (status) params = params.set('status', status);
    return this.http.get<SubscriptionDto[]>(`${BASE}/subscriptions`, { params });
  }
  createSubscription(body: { studentId: string; type: string; startDate: string; endDate?: string }): Observable<SubscriptionDto> {
    return this.http.post<SubscriptionDto>(`${BASE}/subscriptions`, body);
  }
  renewSubscription(id: string, additionalMonths?: number): Observable<{ id: string; endDate: string; message: string }> {
    const params = additionalMonths != null ? new HttpParams().set('additionalMonths', String(additionalMonths)) : undefined;
    return this.http.post<{ id: string; endDate: string; message: string }>(`${BASE}/subscriptions/${id}/renew`, {}, { params });
  }

  // Reports
  getDailyReport(from: string, to: string): Observable<ReportDto[]> {
    return this.http.get<ReportDto[]>(`${BASE}/reports/daily`, {
      params: new HttpParams().set('from', from).set('to', to)
    });
  }
  getMonthlyReport(from?: string, to?: string): Observable<ReportDto[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<ReportDto[]>(`${BASE}/reports/monthly`, { params });
  }

  // Settings
  getHolidays(year?: number): Observable<HolidayDto[]> {
    const params = year != null ? new HttpParams().set('year', String(year)) : undefined;
    return this.http.get<HolidayDto[]>(`${BASE}/settings/holidays`, { params });
  }
  createHoliday(body: { holidayDate: string; name: string; description?: string }): Observable<HolidayDto> {
    return this.http.post<HolidayDto>(`${BASE}/settings/holidays`, body);
  }
  updateHoliday(id: string, body: Partial<HolidayDto>): Observable<HolidayDto> {
    return this.http.put<HolidayDto>(`${BASE}/settings/holidays/${id}`, body);
  }
  deleteHoliday(id: string): Observable<void> {
    return this.http.delete<void>(`${BASE}/settings/holidays/${id}`);
  }
  getSettings(): Observable<SystemSettingDto[]> {
    return this.http.get<SystemSettingDto[]>(`${BASE}/settings/keys`);
  }
  setSetting(key: string, value: string, description?: string): Observable<SystemSettingDto> {
    return this.http.put<SystemSettingDto>(`${BASE}/settings/keys/${key}`, { value, description });
  }
}
