import { Component, OnInit } from '@angular/core';
import { AdminApiService, HolidayDto, SystemSettingDto } from '../../../../core/services/admin-api.service';

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
  holidayYear: number | null = new Date().getFullYear();
  showHolidayForm = false;
  holidayForm: { holidayDate: string; name: string; description: string } = { holidayDate: '', name: '', description: '' };

  constructor(public api: AdminApiService) {}

  ngOnInit(): void {
    this.loadHolidays();
    this.api.getSettings().subscribe(list => this.settings = list);
  }

  loadHolidays(): void {
    this.loading = true;
    this.error = '';
    this.api.getHolidays(this.holidayYear ?? undefined).subscribe({
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
    this.api.createHoliday(this.holidayForm).subscribe(() => { this.loadHolidays(); this.showHolidayForm = false; });
  }

  deleteHoliday(h: HolidayDto): void {
    if (confirm('Delete this holiday?')) this.api.deleteHoliday(h.id).subscribe(() => this.loadHolidays());
  }
}
