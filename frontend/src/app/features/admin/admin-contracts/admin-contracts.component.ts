import { Component, OnInit } from '@angular/core';
import { AdminApiService, ContractDto, TeacherDto, StudentDto, SubscriptionDto } from '../../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-contracts',
  templateUrl: './admin-contracts.component.html',
  styleUrls: ['./admin-contracts.component.css']
})
export class AdminContractsComponent implements OnInit {
  contracts: ContractDto[] = [];
  teachers: TeacherDto[] = [];
  students: StudentDto[] = [];
  subscriptions: SubscriptionDto[] = [];
  loading = true;
  showCreate = false;
  form: any = { teacherId: '', studentId: '', subscriptionId: '', legacyPeriodEnd: '', daysOfWeek: [], scheduleStartTime: '', scheduleEndTime: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.load();
    this.api.getTeachers().subscribe(t => this.teachers = t);
    this.api.getStudents().subscribe(s => this.students = s);
    this.api.getSubscriptions(undefined, 'ONE_TO_ONE', 'ACTIVE').subscribe(s => this.subscriptions = s);
  }

  load(): void {
    this.loading = true;
    this.api.getContracts().subscribe({ next: list => { this.contracts = list; this.loading = false; }, error: () => this.loading = false });
  }

  openCreate(): void {
    this.showCreate = true;
    this.form = { teacherId: '', studentId: '', subscriptionId: '', legacyPeriodEnd: '', daysOfWeek: [], scheduleStartTime: '', scheduleEndTime: '' };
  }

  submitCreate(): void {
    const body: any = { teacherId: this.form.teacherId, studentId: this.form.studentId };
    if (this.form.subscriptionId) body.subscriptionId = this.form.subscriptionId;
    else if (this.form.legacyPeriodEnd) body.legacyPeriodEnd = this.form.legacyPeriodEnd;
    if (this.form.scheduleStartTime) body.scheduleStartTime = this.form.scheduleStartTime;
    if (this.form.scheduleEndTime) body.scheduleEndTime = this.form.scheduleEndTime;
    if (this.form.daysOfWeek?.length) body.daysOfWeek = this.form.daysOfWeek;
    this.api.createContract(body).subscribe({ next: () => { this.load(); this.showCreate = false; }, error: () => {} });
  }

  cancelContract(c: ContractDto): void {
    if (confirm('Cancel this One-To-One contract?')) this.api.cancelContract(c.id).subscribe(() => this.load());
  }
}
