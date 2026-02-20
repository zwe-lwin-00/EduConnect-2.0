import { Component, OnInit } from '@angular/core';
import { TeacherApiService, TeacherGroupClassDto } from '../../../../core/services/teacher-api.service';

@Component({
  selector: 'app-teacher-group-classes',
  templateUrl: './teacher-group-classes.component.html',
  styleUrls: ['./teacher-group-classes.component.css']
})
export class TeacherGroupClassesComponent implements OnInit {
  list: TeacherGroupClassDto[] = [];
  loading = true;
  error = '';
  showEdit = false;
  selected: TeacherGroupClassDto | null = null;
  editForm = { name: '', zoomJoinUrl: '', active: true };

  constructor(public api: TeacherApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = '';
    this.api.getGroupClasses().subscribe({
      next: (list) => { this.list = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load group classes. Please try again.'; }
    });
  }

  closeEdit(): void {
    this.showEdit = false;
    this.selected = null;
  }

  openEdit(gc: TeacherGroupClassDto): void {
    this.selected = gc;
    this.editForm = { name: gc.name, zoomJoinUrl: gc.zoomJoinUrl || '', active: gc.active };
    this.showEdit = true;
  }

  saveEdit(): void {
    if (!this.selected) return;
    this.api.updateGroupClass(this.selected.id, this.editForm).subscribe({
      next: () => { this.load(); this.showEdit = false; this.selected = null; },
      error: () => {}
    });
  }
}
