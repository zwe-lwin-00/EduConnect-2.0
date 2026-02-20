import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { JwtInterceptor } from './core/http/jwt.interceptor';
import { SharedModule } from './shared/shared.module';
import { DxDataGridModule, DxButtonModule } from 'devextreme-angular';

import { LoginComponent } from './features/auth/login/login.component';
import { AdminLayoutComponent } from './features/admin/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard.component';
import { AdminTeachersComponent } from './features/admin/admin-teachers/admin-teachers.component';
import { AdminParentsComponent } from './features/admin/admin-parents/admin-parents.component';
import { AdminStudentsComponent } from './features/admin/admin-students/admin-students.component';
import { AdminContractsComponent } from './features/admin/admin-contracts/admin-contracts.component';
import { AdminGroupClassesComponent } from './features/admin/admin-group-classes/admin-group-classes.component';
import { AdminAttendanceComponent } from './features/admin/admin-attendance/admin-attendance.component';
import { AdminPaymentsComponent } from './features/admin/admin-payments/admin-payments.component';
import { AdminReportsComponent } from './features/admin/admin-reports/admin-reports.component';
import { AdminSettingsComponent } from './features/admin/admin-settings/admin-settings.component';
import { TeacherLayoutComponent } from './features/teacher/teacher-layout/teacher-layout.component';
import { TeacherDashboardComponent } from './features/teacher/teacher-dashboard/teacher-dashboard.component';
import { TeacherAvailabilityComponent } from './features/teacher/teacher-availability/teacher-availability.component';
import { TeacherStudentsComponent } from './features/teacher/teacher-students/teacher-students.component';
import { TeacherSessionsComponent } from './features/teacher/teacher-sessions/teacher-sessions.component';
import { TeacherGroupClassesComponent } from './features/teacher/teacher-group-classes/teacher-group-classes.component';
import { TeacherHomeworkGradesComponent } from './features/teacher/teacher-homework-grades/teacher-homework-grades.component';
import { TeacherProfileComponent } from './features/teacher/teacher-profile/teacher-profile.component';
import { ParentLayoutComponent } from './features/parent/parent-layout/parent-layout.component';
import { ParentStudentsComponent } from './features/parent/parent-students/parent-students.component';
import { ParentStudentOverviewComponent } from './features/parent/parent-student-overview/parent-student-overview.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    AdminLayoutComponent,
    AdminDashboardComponent,
    AdminTeachersComponent,
    AdminParentsComponent,
    AdminStudentsComponent,
    AdminContractsComponent,
    AdminGroupClassesComponent,
    AdminAttendanceComponent,
    AdminPaymentsComponent,
    AdminReportsComponent,
    AdminSettingsComponent,
    TeacherLayoutComponent,
    TeacherDashboardComponent,
    TeacherAvailabilityComponent,
    TeacherStudentsComponent,
    TeacherSessionsComponent,
    TeacherGroupClassesComponent,
    TeacherHomeworkGradesComponent,
    TeacherProfileComponent,
    ParentLayoutComponent,
    ParentStudentsComponent,
    ParentStudentOverviewComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    AppRoutingModule,
    SharedModule,
    DxDataGridModule,
    DxButtonModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
