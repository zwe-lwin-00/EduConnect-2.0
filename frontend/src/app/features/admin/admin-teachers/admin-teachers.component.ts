import { Component, OnInit } from '@angular/core';
import { AdminApiService, TeacherDto } from '../../../core/services/admin-api.service';

interface OnboardForm {
  email: string;
  fullName: string;
  phone: string;
  education: string;
  bio: string;
  specializations: string;
}
interface EditForm {
  fullName: string;
  phone: string;
  education: string;
  bio: string;
  specializations: string;
}

@Component({
  selector: 'app-admin-teachers',
  templateUrl: './admin-teachers.component.html',
  styleUrls: ['./admin-teachers.component.css']
})
export class AdminTeachersComponent implements OnInit {
  teachers: TeacherDto[] = [];
  loading = true;
  error = '';
  showOnboard = false;
  onboardForm: OnboardForm = { email: '', fullName: '', phone: '', education: '', bio: '', specializations: '' };
  tempPassword = '';
  showEdit = false;
  selectedTeacher: TeacherDto | null = null;
  editForm: EditForm = { fullName: '', phone: '', education: '', bio: '', specializations: '' };
  showResetPassword = false;
  resetTeacher: TeacherDto | null = null;
  resetPasswordResult: { temporaryPassword: string; email: string } | null = null;

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getTeachers().subscribe({
      next: (list) => { this.teachers = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load'; }
    });
  }

  openOnboard(): void {
    this.showOnboard = true;
    this.onboardForm = { email: '', fullName: '', phone: '', education: '', bio: '', specializations: '' };
    this.tempPassword = '';
  }

  submitOnboard(): void {
    const body: Record<string, unknown> = { ...this.onboardForm };
    if (this.onboardForm.specializations?.trim()) {
      body['specializations'] = this.onboardForm.specializations.split(',').map(s => s.trim()).filter(Boolean);
    }
    this.api.onboardTeacher(body).subscribe({
      next: (res) => {
        this.tempPassword = res.temporaryPassword;
        this.load();
        if (!this.tempPassword) this.showOnboard = false;
      },
      error: (e) => { this.error = e?.error?.message || 'Failed'; }
    });
  }

  closeOnboard(): void {
    this.showOnboard = false;
    this.tempPassword = '';
  }

  openEdit(t: TeacherDto): void {
    this.selectedTeacher = t;
    this.editForm = {
      fullName: t.fullName || '',
      phone: t.phone || '',
      education: t.education || '',
      bio: t.bio || '',
      specializations: (t.specializations && t.specializations.length) ? t.specializations.join(', ') : ''
    };
    this.showEdit = true;
  }

  saveEdit(): void {
    if (!this.selectedTeacher) return;
    const body: Record<string, unknown> = {
      fullName: this.editForm.fullName,
      phone: this.editForm.phone,
      education: this.editForm.education,
      bio: this.editForm.bio
    };
    if (this.editForm.specializations?.trim()) {
      body['specializations'] = this.editForm.specializations.split(',').map(s => s.trim()).filter(Boolean);
    }
    this.api.updateTeacher(this.selectedTeacher.id, body).subscribe({
      next: () => { this.load(); this.showEdit = false; this.selectedTeacher = null; },
      error: (e) => { this.error = e?.error?.message || 'Failed'; }
    });
  }

  closeEdit(): void {
    this.showEdit = false;
    this.selectedTeacher = null;
  }

  verify(t: TeacherDto): void {
    this.api.verifyTeacher(t.id).subscribe(() => this.load());
  }

  reject(t: TeacherDto): void {
    this.api.rejectTeacher(t.id).subscribe(() => this.load());
  }

  activate(t: TeacherDto): void {
    this.api.activateTeacher(t.id).subscribe(() => this.load());
  }

  suspend(t: TeacherDto): void {
    this.api.suspendTeacher(t.id).subscribe(() => this.load());
  }

  openResetPassword(t: TeacherDto): void {
    this.resetTeacher = t;
    this.resetPasswordResult = null;
    this.showResetPassword = true;
  }

  confirmResetPassword(): void {
    if (!this.resetTeacher) return;
    this.api.resetTeacherPassword(this.resetTeacher.id).subscribe({
      next: (res) => {
        this.resetPasswordResult = { temporaryPassword: res.temporaryPassword, email: res.email };
        this.load();
      },
      error: (e) => { this.error = e?.error?.message || 'Failed'; }
    });
  }

  closeResetPassword(): void {
    this.showResetPassword = false;
    this.resetTeacher = null;
    this.resetPasswordResult = null;
  }
}
