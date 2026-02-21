import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { ParentApiService, StudentOverviewDto } from '../../../core/services/parent-api.service';

@Component({
  selector: 'app-parent-student-overview',
  templateUrl: './parent-student-overview.component.html',
  styleUrls: ['./parent-student-overview.component.css']
})
export class ParentStudentOverviewComponent implements OnInit {
  overview: StudentOverviewDto | null = null;
  loading = true;
  error = '';
  invalidStudent = false;
  studentId = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    public api: ParentApiService
  ) {}

  navigateToParent(): void {
    this.router.navigate(['/parent']);
  }

  navigateToCalendar(): void {
    if (this.overview?.studentId) {
      this.router.navigate(['/parent', 'student', this.overview.studentId, 'calendar']);
    }
  }

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
        this.error = '';
      },
      error: () => { this.loading = false; this.error = 'Failed to load overview. Please try again.'; }
    });
  }
}
