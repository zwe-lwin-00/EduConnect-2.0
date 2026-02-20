import { Component, OnInit } from '@angular/core';
import { ParentApiService, ParentStudentDto } from '../../../../core/services/parent-api.service';

@Component({
  selector: 'app-parent-students',
  templateUrl: './parent-students.component.html',
  styleUrls: ['./parent-students.component.css']
})
export class ParentStudentsComponent implements OnInit {
  students: ParentStudentDto[] = [];
  loading = true;

  constructor(public api: ParentApiService) {}

  ngOnInit(): void {
    this.api.getStudents().subscribe({
      next: (list) => { this.students = list; this.loading = false; },
      error: () => this.loading = false
    });
  }
}
