import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherProfileDto } from '../../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-profile',
  templateUrl: './teacher-profile.component.html',
  styleUrls: ['./teacher-profile.component.css']
})
export class TeacherProfileComponent implements OnInit {
  profile: TeacherProfileDto | null = null;
  loading = true;

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.api.getProfile().subscribe({
      next: (p) => { this.profile = p; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
