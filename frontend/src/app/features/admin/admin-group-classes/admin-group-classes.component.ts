import { Component, OnInit } from '@angular/core';
import { AdminApiService, GroupClassDto, TeacherDto } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-group-classes',
  templateUrl: './admin-group-classes.component.html',
  styleUrls: ['./admin-group-classes.component.css']
})
export class AdminGroupClassesComponent implements OnInit {
  list: GroupClassDto[] = [];
  teachers: TeacherDto[] = [];
  loading = true;
  showCreate = false;
  form: any = { name: '', teacherId: '', scheduleStartTime: '', scheduleEndTime: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getTeachers().subscribe(t => this.teachers = t);
  }

  load(): void {
    this.loading = true;
    this.api.getGroupClasses().subscribe({ next: list => { this.list = list; this.loading = false; }, error: () => this.loading = false });
  }

  openCreate(): void {
    this.showCreate = true;
    this.form = { name: '', teacherId: '', scheduleStartTime: '', scheduleEndTime: '' };
  }

  submitCreate(): void {
    const body: any = { name: this.form.name, teacherId: this.form.teacherId };
    if (this.form.scheduleStartTime) body.scheduleStartTime = this.form.scheduleStartTime;
    if (this.form.scheduleEndTime) body.scheduleEndTime = this.form.scheduleEndTime;
    this.api.createGroupClass(body).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }
}
