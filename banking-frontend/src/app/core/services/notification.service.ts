import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type NotificationType = 'error' | 'success' | 'warning';

export interface Notification {
  message: string;
  type: NotificationType;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly _notification$ = new BehaviorSubject<Notification | null>(null);
  readonly notification$: Observable<Notification | null> = this._notification$.asObservable();

  private clearTimer: ReturnType<typeof setTimeout> | null = null;

  showError(message: string): void {
    this.show({ message, type: 'error' }, 6000);
  }

  showSuccess(message: string): void {
    this.show({ message, type: 'success' }, 3000);
  }

  showWarning(message: string): void {
    this.show({ message, type: 'warning' }, 4000);
  }

  clear(): void {
    if (this.clearTimer) clearTimeout(this.clearTimer);
    this._notification$.next(null);
  }

  private show(notification: Notification, durationMs: number): void {
    if (this.clearTimer) clearTimeout(this.clearTimer);
    this._notification$.next(notification);
    this.clearTimer = setTimeout(() => this._notification$.next(null), durationMs);
  }
}
