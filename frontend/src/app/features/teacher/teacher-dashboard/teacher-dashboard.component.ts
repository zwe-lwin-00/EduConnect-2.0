import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherDashboardData } from '../../../../core/services/teacher-api.service';

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

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.api.getDashboard().subscribe({
      next: (d) => { this.data = d; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
