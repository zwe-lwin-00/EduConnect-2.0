import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherStudentDto } from '../../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-students',
  templateUrl: './teacher-students.component.html',
  styleUrls: ['./teacher-students.component.css']
})
export class TeacherStudentsComponent implements OnInit {
  students: TeacherStudentDto[] = [];
  loading = true;
  error = '';

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.api.getStudents().subscribe({
      next: (list) => { this.students = list; this.loading = false; this.error = ''; },
      error: () => { this.loading = false; this.error = 'Failed to load students. Please try again.'; }
    });
  }
}
