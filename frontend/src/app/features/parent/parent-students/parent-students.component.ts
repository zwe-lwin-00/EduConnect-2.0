import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ParentApiService, ParentStudentDto } from '../../../core/services/parent-api.service';

@Component({
  selector: 'app-parent-students',
  templateUrl: './parent-students.component.html',
  styleUrls: ['./parent-students.component.css']
})
export class ParentStudentsComponent implements OnInit {
  students: ParentStudentDto[] = [];
  loading = true;
  error = '';

  constructor(public api: ParentApiService, private router: Router) {}

  navigateToStudent(studentId: string): void {
    this.router.navigate(['/parent/student', studentId]);
  }

  ngOnInit(): void {
    this.api.getStudents().subscribe({
      next: (list) => { this.students = list; this.loading = false; this.error = ''; },
      error: () => { this.loading = false; this.error = 'Failed to load students. Please try again.'; }
    });
  }
}
