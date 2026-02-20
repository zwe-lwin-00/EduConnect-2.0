import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherProfileDto } from '../../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-profile',
  templateUrl: './teacher-profile.component.html',
  styleUrls: ['./teacher-profile.component.css']
})
export class TeacherProfileComponent implements OnInit {
  profile: TeacherProfileDto | null = null;
  zoomJoinUrl = '';
  loading = true;
  error = '';
  saving = false;

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.api.getProfile().subscribe({
      next: (p) => {
        this.profile = p;
        this.zoomJoinUrl = p?.zoomJoinUrl ?? '';
        this.loading = false;
        this.error = '';
      },
      error: () => { this.loading = false; this.error = 'Failed to load profile. Please try again.'; }
    });
  }

  saveZoom(): void {
    if (this.saving) return;
    this.saving = true;
    this.api.updateProfile({ zoomJoinUrl: this.zoomJoinUrl || undefined }).subscribe({
      next: (p) => {
        this.profile = p;
        this.zoomJoinUrl = p?.zoomJoinUrl ?? '';
        this.saving = false;
      },
      error: () => this.saving = false
    });
  }
}
