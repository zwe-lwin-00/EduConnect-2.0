import { Component, OnInit } from '@angular/core';
import { AdminApiService, ParentDto, CreateParentResponse } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-parents',
  templateUrl: './admin-parents.component.html',
  styleUrls: ['./admin-parents.component.css']
})
export class AdminParentsComponent implements OnInit {
  parents: ParentDto[] = [];
  loading = true;
  showCreate = false;
  form: { email: string; fullName: string; phone: string } = { email: '', fullName: '', phone: '' };
  createResult: CreateParentResponse | null = null;

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getParents().subscribe({
      next: (list) => { this.parents = list; this.loading = false; },
      error: () => this.loading = false
    });
  }

  openCreate(): void {
    this.showCreate = true;
    this.form = { email: '', fullName: '', phone: '' };
    this.createResult = null;
  }

  submitCreate(): void {
    this.api.createParent(this.form).subscribe({
      next: (res) => { this.createResult = res; this.load(); },
      error: () => {}
    });
  }

  closeCreate(): void {
    this.showCreate = false;
    this.createResult = null;
  }
}
