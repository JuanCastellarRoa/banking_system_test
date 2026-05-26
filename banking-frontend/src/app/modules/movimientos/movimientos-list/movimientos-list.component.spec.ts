import { MovimientosListComponent } from './movimientos-list.component';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of, throwError } from 'rxjs';
import { Movimiento } from '../../../shared/models/movimiento.model';

const mockMovimiento: Movimiento = {
  movimientoId: 1,
  tipoMovimiento: 'CREDITO',
  valor: 500,
  saldo: 1000,
  numeroCuenta: 225487,
};

describe('MovimientosListComponent', () => {
  let component: MovimientosListComponent;
  let movimientoService: jest.Mocked<MovimientoService>;
  let notificationService: jest.Mocked<NotificationService>;

  beforeEach(() => {
    movimientoService = {
      getAll: jest.fn().mockReturnValue(of([mockMovimiento])),
      delete: jest.fn().mockReturnValue(of(undefined)),
    } as any;

    notificationService = {
      showSuccess: jest.fn(),
      showError: jest.fn(),
    } as any;

    component = new MovimientosListComponent(movimientoService, notificationService);
  });

  it('should create', () => expect(component).toBeTruthy());

  it('ngOnInit loads movimientos', () => {
    component.ngOnInit();
    expect(movimientoService.getAll).toHaveBeenCalled();
    expect(component.movimientos).toHaveLength(1);
    expect(component.loading).toBe(false);
  });

  it('load handles error', () => {
    movimientoService.getAll.mockReturnValue(throwError(() => new Error('err')));
    component.load();
    expect(component.loading).toBe(false);
  });

  it('onSearch filters by numeroCuenta', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: '225487' } } as any);
    expect(component.filtered).toHaveLength(1);
  });

  it('onSearch with empty term shows all', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: '' } } as any);
    expect(component.filtered).toHaveLength(1);
  });

  it('openCreate sets showForm and clears selection', () => {
    component.openCreate();
    expect(component.showForm).toBe(true);
    expect(component.selected).toBeNull();
  });

  it('openEdit sets selected and showForm', () => {
    component.openEdit(mockMovimiento);
    expect(component.selected).toEqual(mockMovimiento);
    expect(component.showForm).toBe(true);
  });

  it('closeForm hides form', () => {
    component.showForm = true;
    component.closeForm();
    expect(component.showForm).toBe(false);
  });

  it('onSaved reloads and shows success', () => {
    component.ngOnInit();
    component.onSaved();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Movimiento guardado correctamente');
  });

  it('delete calls service on confirm', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    component.ngOnInit();
    component.delete(mockMovimiento);
    expect(movimientoService.delete).toHaveBeenCalledWith(1);
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Movimiento eliminado');
  });

  it('delete shows error on failure', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    movimientoService.delete.mockReturnValue(throwError(() => ({ error: { mensaje: 'Error mov' } })));
    component.ngOnInit();
    component.delete(mockMovimiento);
    expect(notificationService.showError).toHaveBeenCalledWith('Error mov');
  });

  it('delete does nothing if cancelled', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(false);
    component.delete(mockMovimiento);
    expect(movimientoService.delete).not.toHaveBeenCalled();
  });

  it('onSearch filters movimientos by tipoMovimiento when numeroCuenta does not match', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: 'credito' } } as any);
    expect(component.filtered).toHaveLength(1);
  });

  it('onSearch returns empty when no movimientos match', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: 'zzznomatch' } } as any);
    expect(component.filtered).toHaveLength(0);
  });

  it('onSearch handles movimiento without numeroCuenta (optional chaining branch)', () => {
    const movSinCuenta = { ...mockMovimiento, numeroCuenta: undefined };
    movimientoService.getAll.mockReturnValue(of([movSinCuenta]));
    component.ngOnInit();
    component.onSearch({ target: { value: 'credito' } } as any);
    expect(component.filtered).toHaveLength(1);
  });

  it('delete shows default error when err.error.mensaje is absent', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    movimientoService.delete.mockReturnValue(throwError(() => ({ error: {} })));
    component.ngOnInit();
    component.delete(mockMovimiento);
    expect(notificationService.showError).toHaveBeenCalledWith('No se pudo eliminar el movimiento');
  });
});
