import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { getApiUrl } from './api-url';

const BASE = getApiUrl('notifications');

export interface NotificationDto {
  id: string;
  title: string;
  message?: string;
  read: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationsApiService {
  constructor(private http: HttpClient) {}

  getNotifications(): Observable<NotificationDto[]> {
    return this.http.get<NotificationDto[]>(BASE);
  }

  markAllRead(): Observable<void> {
    return this.http.post<void>(`${BASE}/mark-all-read`, {});
  }
}
