import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    jest.useFakeTimers();
    service = new NotificationService();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('should emit null initially', (done) => {
    service.notification$.subscribe((n) => {
      expect(n).toBeNull();
      done();
    });
  });

  it('showError emits error notification', (done) => {
    const values: any[] = [];
    service.notification$.subscribe((n) => values.push(n));
    service.showError('error message');
    expect(values[values.length - 1]).toEqual({ message: 'error message', type: 'error' });
    done();
  });

  it('showSuccess emits success notification', () => {
    const values: any[] = [];
    service.notification$.subscribe((n) => values.push(n));
    service.showSuccess('ok');
    expect(values[values.length - 1]).toEqual({ message: 'ok', type: 'success' });
  });

  it('showWarning emits warning notification', () => {
    const values: any[] = [];
    service.notification$.subscribe((n) => values.push(n));
    service.showWarning('warn');
    expect(values[values.length - 1]).toEqual({ message: 'warn', type: 'warning' });
  });

  it('clear sets notification to null', () => {
    const values: any[] = [];
    service.notification$.subscribe((n) => values.push(n));
    service.showSuccess('message');
    service.clear();
    expect(values[values.length - 1]).toBeNull();
  });

  it('notification auto-clears after timeout', () => {
    const values: any[] = [];
    service.notification$.subscribe((n) => values.push(n));
    service.showSuccess('msg');
    expect(values[values.length - 1]).not.toBeNull();
    jest.advanceTimersByTime(4000);
    expect(values[values.length - 1]).toBeNull();
  });

  it('showError overrides previous notification', () => {
    const values: any[] = [];
    service.notification$.subscribe((n) => values.push(n));
    service.showSuccess('first');
    service.showError('second');
    expect(values[values.length - 1]).toEqual({ message: 'second', type: 'error' });
  });
});
