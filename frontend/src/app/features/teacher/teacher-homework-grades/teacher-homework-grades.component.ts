import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherHomeworkDto, TeacherGradeDto, TeacherStudentDto } from '../../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-homework-grades',
  templateUrl: './teacher-homework-grades.component.html',
  styleUrls: ['./teacher-homework-grades.component.css']
})
export class TeacherHomeworkGradesComponent implements OnInit {
  homework: TeacherHomeworkDto[] = [];
  grades: TeacherGradeDto[] = [];
  students: TeacherStudentDto[] = [];
  loading = true;
  error = '';
  showHomeworkForm = false;
  showGradeForm = false;
  homeworkForm = { studentId: '', title: '', description: '', dueDate: '' };
  gradeForm = { studentId: '', title: '', gradeValue: 100, maxValue: 100, gradeDate: '', notes: '' };
  showFeedback = false;
  selectedHomework: TeacherHomeworkDto | null = null;
  teacherFeedback = '';

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getStudents().subscribe(s => this.students = s);
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getHomework().subscribe({
      next: (list) => { this.homework = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load homework. Please try again.'; }
    });
    this.api.getGrades().subscribe(g => this.grades = g);
  }

  closeHomeworkForm(): void { this.showHomeworkForm = false; }
  closeGradeForm(): void { this.showGradeForm = false; }
  closeFeedback(): void { this.showFeedback = false; this.selectedHomework = null; }

  openHomeworkForm(): void {
    this.homeworkForm = { studentId: '', title: '', description: '', dueDate: new Date().toISOString().slice(0, 10) };
    this.showHomeworkForm = true;
  }

  submitHomework(): void {
    this.api.createHomework(this.homeworkForm).subscribe({
      next: () => { this.load(); this.showHomeworkForm = false; },
      error: () => {}
    });
  }

  openGradeForm(): void {
    this.gradeForm = {
      studentId: '',
      title: '',
      gradeValue: 100,
      maxValue: 100,
      gradeDate: new Date().toISOString().slice(0, 10),
      notes: ''
    };
    this.showGradeForm = true;
  }

  submitGrade(): void {
    this.api.createGrade(this.gradeForm).subscribe({
      next: () => { this.load(); this.showGradeForm = false; },
      error: () => {}
    });
  }

  markSubmitted(h: TeacherHomeworkDto): void {
    this.api.updateHomework(h.id, { status: 'SUBMITTED' }).subscribe(() => this.load());
  }

  openFeedback(h: TeacherHomeworkDto): void {
    this.selectedHomework = h;
    this.teacherFeedback = h.teacherFeedback || '';
    this.showFeedback = true;
  }

  saveFeedback(): void {
    if (!this.selectedHomework) return;
    this.api.updateHomework(this.selectedHomework.id, { status: 'GRADED', teacherFeedback: this.teacherFeedback }).subscribe(() => { this.load(); this.showFeedback = false; });
  }
}
