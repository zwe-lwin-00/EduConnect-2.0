import { Component, OnInit } from '@angular/core';
import { AdminApiService, HolidayDto, SystemSettingDto } from '../../../core/services/admin-api.service';

@Component({
  selector: 'app-admin-settings',
  templateUrl: './admin-settings.component.html',
  styleUrls: ['./admin-settings.component.css']
})
export class AdminSettingsComponent implements OnInit {
  holidays: HolidayDto[] = [];
  settings: SystemSettingDto[] = [];
  loading = true;
  error = '';
  holidayYear: number = new Date().getFullYear();
  showHolidayForm = false;
  showDeleteConfirm = false;
  holidayToDelete: HolidayDto | null = null;
  holidayForm: { holidayDate: string | Date | number; name: string; description: string } = { holidayDate: '', name: '', description: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.loadHolidays();
    this.api.getSettings().subscribe(list => this.settings = list);
  }

  loadHolidays(): void {
    this.loading = true;
    this.error = '';
    this.api.getHolidays(this.holidayYear).subscribe({
      next: list => { this.holidays = list; this.loading = false; },
      error: () => { this.loading = false; this.error = 'Failed to load holidays. Please try again.'; }
    });
  }

  onYearChange(): void {
    this.loadHolidays();
  }

  openHolidayForm(): void {
    this.showHolidayForm = true;
    this.holidayForm = { holidayDate: '', name: '', description: '' };
  }

  closeHolidayForm(): void {
    this.showHolidayForm = false;
  }

  submitHoliday(): void {
    const dateVal = this.holidayForm.holidayDate;
    let holidayDate: string;
    if (typeof dateVal === 'string') holidayDate = dateVal;
    else if (typeof dateVal === 'number') holidayDate = new Date(dateVal).toISOString().slice(0, 10);
    else holidayDate = (dateVal as Date).toISOString().slice(0, 10);
    this.api.createHoliday({ ...this.holidayForm, holidayDate }).subscribe(() => { this.loadHolidays(); this.showHolidayForm = false; });
  }

  confirmDeleteHoliday(h: HolidayDto): void {
    this.holidayToDelete = h;
    this.showDeleteConfirm = true;
  }

  doDeleteHoliday(): void {
    if (!this.holidayToDelete) return;
    this.api.deleteHoliday(this.holidayToDelete.id).subscribe(() => {
      this.loadHolidays();
      this.showDeleteConfirm = false;
      this.holidayToDelete = null;
    });
  }
}
