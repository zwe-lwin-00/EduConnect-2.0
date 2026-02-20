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

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.api.getStudents().subscribe({
      next: (list) => { this.students = list; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
