import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationService, Notification } from '../../../core/services/notification.service';

@Component({
  standalone: false,
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
})
export class NotificationComponent implements OnInit, OnDestroy {
  notification: Notification | null = null;
  private subscription!: Subscription;

  constructor(private readonly notificationService: NotificationService) {}

  ngOnInit(): void {
    this.subscription = this.notificationService.notification$.subscribe(
      n => (this.notification = n)
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  dismiss(): void {
    this.notificationService.clear();
  }
}
