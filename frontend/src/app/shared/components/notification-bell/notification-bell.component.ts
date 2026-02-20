import { Component, OnInit } from '@angular/core';
import { NotificationsApiService, NotificationDto } from '../../../core/services/notifications-api.service';

@Component({
  selector: 'app-notification-bell',
  templateUrl: './notification-bell.component.html',
  styleUrls: ['./notification-bell.component.css']
})
export class NotificationBellComponent implements OnInit {
  notifications: NotificationDto[] = [];
  unreadCount = 0;
  open = false;
  loading = false;

  constructor(private api: NotificationsApiService) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.api.getNotifications().subscribe({
      next: (list) => {
        this.notifications = list;
        this.unreadCount = list.filter((n) => !n.read).length;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  toggle(): void {
    this.open = !this.open;
    if (this.open) this.load();
  }

  markAllRead(): void {
    this.api.markAllRead().subscribe({
      next: () => {
        this.notifications = this.notifications.map((n) => ({ ...n, read: true }));
        this.unreadCount = 0;
      }
    });
  }

  close(): void {
    this.open = false;
  }
}
