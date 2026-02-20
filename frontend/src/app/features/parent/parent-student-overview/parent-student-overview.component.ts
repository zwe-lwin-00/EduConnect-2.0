import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { ParentApiService, StudentOverviewDto } from '../../../../core/services/parent-api.service';

@Component({
  selector: 'app-parent-student-overview',
  templateUrl: './parent-student-overview.component.html',
  styleUrls: ['./parent-student-overview.component.css']
})
export class ParentStudentOverviewComponent implements OnInit {
  overview: StudentOverviewDto | null = null;
  loading = true;
  invalidStudent = false;
  studentId = '';

  constructor(
    private route: ActivatedRoute,
    public api: ParentApiService
  ) {}

  ngOnInit(): void {
    this.studentId = this.route.snapshot.paramMap.get('studentId') || '';
    if (!this.studentId) {
      this.invalidStudent = true;
      this.loading = false;
      return;
    }
    this.api.getStudents().pipe(
      switchMap((students) => {
        const allowed = students.some((s) => s.id === this.studentId);
        if (!allowed) {
          this.invalidStudent = true;
          this.loading = false;
          return of(null);
        }
        return this.api.getStudentOverview(this.studentId);
      })
    ).subscribe({
      next: (o) => {
        if (o) {
          this.overview = o;
        }
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }
}
