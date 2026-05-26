import { NotificationComponent } from './notification.component';
import { NotificationService } from '../../../core/services/notification.service';
import { BehaviorSubject } from 'rxjs';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let notificationService: jest.Mocked<NotificationService>;
  let subject: BehaviorSubject<any>;

  beforeEach(() => {
    subject = new BehaviorSubject<any>(null);
    notificationService = {
      notification$: subject.asObservable(),
      clear: jest.fn(),
    } as any;
    component = new NotificationComponent(notificationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit subscribes to notifications and updates notification property', () => {
    component.ngOnInit();
    subject.next({ message: 'test', type: 'success' });
    expect(component.notification).toEqual({ message: 'test', type: 'success' });
  });

  it('ngOnInit sets null when cleared', () => {
    component.ngOnInit();
    subject.next({ message: 'test', type: 'error' });
    subject.next(null);
    expect(component.notification).toBeNull();
  });

  it('ngOnDestroy unsubscribes from notification stream', () => {
    component.ngOnInit();
    const unsubSpy = jest.spyOn((component as any).subscription, 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubSpy).toHaveBeenCalled();
  });

  it('dismiss calls notificationService.clear()', () => {
    component.dismiss();
    expect(notificationService.clear).toHaveBeenCalled();
  });
});
