import { Component, OnInit } from '@angular/core';
import { AdminApiService, StudentDto, ParentDto } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-students',
  templateUrl: './admin-students.component.html',
  styleUrls: ['./admin-students.component.css']
})
export class AdminStudentsComponent implements OnInit {
  students: StudentDto[] = [];
  parents: ParentDto[] = [];
  loading = true;
  showAdd = false;
  form: { fullName: string; grade: string; dateOfBirth: string; parentId: string } = { fullName: '', grade: 'P1', dateOfBirth: '', parentId: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getParents().subscribe(list => this.parents = list);
  }

  load(): void {
    this.loading = true;
    this.api.getStudents().subscribe({
      next: (list) => { this.students = list; this.loading = false; },
      error: () => this.loading = false
    });
  }

  openAdd(): void {
    this.showAdd = true;
    this.form = { fullName: '', grade: 'P1', dateOfBirth: '', parentId: this.parents[0]?.id || '' };
  }

  submitAdd(): void {
    const body: any = { fullName: this.form.fullName, grade: this.form.grade, parentId: this.form.parentId };
    if (this.form.dateOfBirth) body.dateOfBirth = this.form.dateOfBirth;
    this.api.createStudent(body).subscribe({ next: () => { this.load(); this.showAdd = false; }, error: () => {} });
  }

  setActive(s: StudentDto, active: boolean): void {
    this.api.updateStudent(s.id, { active }).subscribe(() => this.load());
  }
}
