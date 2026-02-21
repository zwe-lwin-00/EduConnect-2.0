import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TeacherApiService, TeacherDashboardData } from '../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-dashboard',
  templateUrl: './teacher-dashboard.component.html',
  styleUrls: ['./teacher-dashboard.component.css']
})
export class TeacherDashboardComponent implements OnInit {
  data: TeacherDashboardData = {
    todayOneToOneSessions: 0,
    todayGroupSessions: 0,
    upcomingOneToOneCount: 0,
    pendingHomeworkToGrade: 0
  };
  loading = true;
  error = '';

  constructor(public api: TeacherApiService, private router: Router) {}

  navigate(path: string): void {
    this.router.navigateByUrl(path);
  }

  ngOnInit(): void {
    this.api.getDashboard().subscribe({
      next: (d) => { this.data = d; this.loading = false; this.error = ''; },
      error: () => { this.loading = false; this.error = 'Failed to load dashboard. Please try again.'; }
    });
  }
}
