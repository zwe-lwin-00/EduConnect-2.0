import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ParentApiService, StudentOverviewDto } from '../../../../core/services/parent-api.service';

@Component({
  selector: 'app-parent-student-overview',
  templateUrl: './parent-student-overview.component.html',
  styleUrls: ['./parent-student-overview.component.css']
})
export class ParentStudentOverviewComponent implements OnInit {
  overview: StudentOverviewDto | null = null;
  loading = true;
  studentId = '';

  constructor(
    private route: ActivatedRoute,
    public api: ParentApiService
  ) {}

  ngOnInit(): void {
    this.studentId = this.route.snapshot.paramMap.get('studentId') || '';
    if (this.studentId) {
      this.api.getStudentOverview(this.studentId).subscribe({
        next: (o) => { this.overview = o; this.loading = false; },
        error: () => this.loading = false
      });
    } else {
      this.loading = false;
    }
  }
}
