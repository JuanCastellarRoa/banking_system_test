import { ErrorInterceptor } from './error.interceptor';
import { NotificationService } from '../services/notification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';

describe('ErrorInterceptor', () => {
  let interceptor: ErrorInterceptor;
  let notificationService: jest.Mocked<NotificationService>;

  beforeEach(() => {
    notificationService = {
      showError: jest.fn(),
      showSuccess: jest.fn(),
      showWarning: jest.fn(),
      clear: jest.fn(),
      notification$: {} as any,
    } as any;
    interceptor = new ErrorInterceptor(notificationService);
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  function makeNext(error: HttpErrorResponse) {
    return { handle: jest.fn().mockReturnValue(throwError(() => error)) };
  }

  function makeError(status: number, errorBody: any, statusText = 'Error'): HttpErrorResponse {
    return new HttpErrorResponse({ status, statusText, error: errorBody });
  }

  it('intercept calls showError and rethrows the error', (done) => {
    const err = makeError(500, { message: 'server down' });
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({
      error: (thrown) => {
        expect(notificationService.showError).toHaveBeenCalledWith('server down');
        expect(thrown).toBe(err);
        done();
      },
    });
  });

  it('extractErrorMessage returns error.message from object body', () => {
    const err = makeError(500, { message: 'msg from message field' });
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('msg from message field');
  });

  it('extractErrorMessage returns error.error when message is absent', () => {
    const err = makeError(500, { error: 'error field value' });
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('error field value');
  });

  it('extractErrorMessage returns error.detail when message and error absent', () => {
    const err = makeError(500, { detail: 'detail value' });
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('detail value');
  });

  it('extractErrorMessage returns default message when no known key in object', () => {
    const err = makeError(500, { other: 'irrelevant' });
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('Error en el servidor');
  });

  it('extractErrorMessage returns plain string body directly', () => {
    const err = makeError(500, 'plain string error');
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('plain string error');
  });

  it('extractErrorMessage returns connection message for status 0', () => {
    const err = makeError(0, null);
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('No se puede conectar con el servidor');
  });

  it('extractErrorMessage returns 404 message', () => {
    const err = makeError(404, null);
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('Recurso no encontrado');
  });

  it('extractErrorMessage returns 400 message', () => {
    const err = makeError(400, null);
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('Solicitud inválida');
  });

  it('extractErrorMessage returns generic message for other status codes', () => {
    const err = makeError(503, null, 'Service Unavailable');
    interceptor.intercept({} as any, makeNext(err) as any).subscribe({ error: () => {} });
    expect(notificationService.showError).toHaveBeenCalledWith('Error 503: Service Unavailable');
  });
});
