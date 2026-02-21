import { Component, OnInit } from '@angular/core';
import { AdminApiService, StudentDto, ParentDto } from '../../../core/services/admin-api.service';

const GRADE_OPTIONS = [{ value: 'P1', label: 'P1' }, { value: 'P2', label: 'P2' }, { value: 'P3', label: 'P3' }, { value: 'P4', label: 'P4' }];

@Component({
  selector: 'app-admin-students',
  templateUrl: './admin-students.component.html',
  styleUrls: ['./admin-students.component.css']
})
export class AdminStudentsComponent implements OnInit {
  students: StudentDto[] = [];
  parents: ParentDto[] = [];
  loading = true;
  error = '';
  showAdd = false;
  form: { fullName: string; grade: string; dateOfBirth: string | Date | number; parentId: string } = { fullName: '', grade: 'P1', dateOfBirth: '', parentId: '' };
  gradeOptions = GRADE_OPTIONS;

  get parentOptions(): { id: string; label: string }[] {
    return this.parents.map(p => ({ id: p.id, label: `${p.fullName} (${p.email})` }));
  }

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getParents().subscribe(list => this.parents = list);
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getStudents().subscribe({
      next: (list) => { this.students = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load students. Please try again.'; }
    });
  }

  openAdd(): void {
    this.showAdd = true;
    this.form = { fullName: '', grade: 'P1', dateOfBirth: '', parentId: '' };
  }

  closeAdd(): void {
    this.showAdd = false;
  }

  submitAdd(): void {
    const body: any = { fullName: this.form.fullName, grade: this.form.grade, parentId: this.form.parentId };
    const dob = this.form.dateOfBirth;
    if (dob != null && dob !== '') {
      body.dateOfBirth = typeof dob === 'string' ? dob : typeof dob === 'number' ? new Date(dob).toISOString().slice(0, 10) : (dob as Date).toISOString().slice(0, 10);
    }
    this.api.createStudent(body).subscribe({ next: () => { this.load(); this.showAdd = false; }, error: () => {} });
  }

  setActive(s: StudentDto, active: boolean): void {
    this.api.updateStudent(s.id, { active }).subscribe(() => this.load());
  }
}
