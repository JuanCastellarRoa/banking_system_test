import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { NotificationService } from '../services/notification.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private readonly notificationService: NotificationService) {}

  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const message = this.extractErrorMessage(error);
        this.notificationService.showError(message);
        return throwError(() => error);
      })
    );
  }

  private extractErrorMessage(error: HttpErrorResponse): string {
    if (error.error && typeof error.error === 'object') {
      return (
        (error.error as Record<string, string>)['message'] ||
        (error.error as Record<string, string>)['error'] ||
        (error.error as Record<string, string>)['detail'] ||
        'Error en el servidor'
      );
    }
    if (typeof error.error === 'string') return error.error;
    if (error.status === 0) return 'No se puede conectar con el servidor';
    if (error.status === 404) return 'Recurso no encontrado';
    if (error.status === 400) return 'Solicitud inválida';
    return `Error ${error.status}: ${error.statusText}`;
  }
}
