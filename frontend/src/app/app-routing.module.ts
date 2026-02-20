import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';
import { LoginComponent } from './features/auth/login/login.component';
import { AdminLayoutComponent } from './features/admin/admin-layout/admin-layout.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard.component';
import { PlaceholderComponent } from './shared/components/placeholder/placeholder.component';
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
import { ParentLayoutComponent } from './features/parent/parent-layout/parent-layout.component';
import { ParentStudentsComponent } from './features/parent/parent-students/parent-students.component';
import { ParentStudentOverviewComponent } from './features/parent/parent-student-overview/parent-student-overview.component';
import { TeacherAvailabilityComponent } from './features/teacher/teacher-availability/teacher-availability.component';
import { TeacherStudentsComponent } from './features/teacher/teacher-students/teacher-students.component';
import { TeacherSessionsComponent } from './features/teacher/teacher-sessions/teacher-sessions.component';
import { TeacherGroupClassesComponent } from './features/teacher/teacher-group-classes/teacher-group-classes.component';
import { TeacherHomeworkGradesComponent } from './features/teacher/teacher-homework-grades/teacher-homework-grades.component';
import { TeacherProfileComponent } from './features/teacher/teacher-profile/teacher-profile.component';
import { TeacherCalendarComponent } from './features/teacher/teacher-calendar/teacher-calendar.component';
import { ParentStudentCalendarComponent } from './features/parent/parent-student-calendar/parent-student-calendar.component';
import { AuthRoutes } from './shared/constants/auth.constants';
import { Roles } from './shared/constants/auth.constants';

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'auth/login' },
  { path: 'auth/login', component: LoginComponent },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.ADMIN] },
    children: [
      { path: '', pathMatch: 'full', component: AdminDashboardComponent },
      { path: 'teachers', component: AdminTeachersComponent, data: { title: 'Teachers' } },
      { path: 'parents', component: AdminParentsComponent, data: { title: 'Parents' } },
      { path: 'students', component: AdminStudentsComponent, data: { title: 'Students' } },
      { path: 'contracts', component: AdminContractsComponent, data: { title: 'One-To-One' } },
      { path: 'group-classes', component: AdminGroupClassesComponent, data: { title: 'Group Classes' } },
      { path: 'attendance', component: AdminAttendanceComponent, data: { title: 'Attendance' } },
      { path: 'payments', component: AdminPaymentsComponent, data: { title: 'Payments' } },
      { path: 'reports', component: AdminReportsComponent, data: { title: 'Reports' } },
      { path: 'settings', component: AdminSettingsComponent, data: { title: 'Settings' } }
    ]
  },
  {
    path: 'teacher',
    component: TeacherLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.TEACHER] },
    children: [
      { path: '', pathMatch: 'full', component: TeacherDashboardComponent },
      { path: 'availability', component: TeacherAvailabilityComponent, data: { title: 'Availability' } },
      { path: 'students', component: TeacherStudentsComponent, data: { title: 'Students' } },
      { path: 'sessions', component: TeacherSessionsComponent, data: { title: 'Sessions' } },
      { path: 'calendar', component: TeacherCalendarComponent, data: { title: 'Calendar' } },
      { path: 'group-classes', component: TeacherGroupClassesComponent, data: { title: 'Group classes' } },
      { path: 'homework-grades', component: TeacherHomeworkGradesComponent, data: { title: 'Homework & Grades' } },
      { path: 'profile', component: TeacherProfileComponent, data: { title: 'Profile' } }
    ]
  },
  {
    path: 'parent',
    component: ParentLayoutComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: [Roles.PARENT] },
    children: [
      { path: '', pathMatch: 'full', component: ParentStudentsComponent },
      { path: 'student/:studentId', component: ParentStudentOverviewComponent, data: { title: 'Student Learning' } },
      { path: 'student/:studentId/calendar', component: ParentStudentCalendarComponent, data: { title: 'Student Calendar' } }
    ]
  },
  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
