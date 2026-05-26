import { FormBuilder } from '@angular/forms';
import { MovimientosFormComponent } from './movimientos-form.component';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { CuentaService } from '../../../core/services/cuenta.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of, throwError } from 'rxjs';
import { Movimiento } from '../../../shared/models/movimiento.model';
import { Cuenta } from '../../../shared/models/cuenta.model';

const mockMovimiento: Movimiento = {
  movimientoId: 1,
  tipoMovimiento: 'CREDITO',
  valor: 500,
  saldo: 1000,
  numeroCuenta: 225487,
};

const mockCuenta: Cuenta = {
  numeroCuenta: 225487, tipoCuenta: 'AHORRO', saldoInicial: 500, estado: true, clienteId: 'c1',
};

describe('MovimientosFormComponent', () => {
  let component: MovimientosFormComponent;
  let movimientoService: jest.Mocked<MovimientoService>;
  let cuentaService: jest.Mocked<CuentaService>;
  let notificationService: jest.Mocked<NotificationService>;

  beforeEach(() => {
    movimientoService = {
      create: jest.fn().mockReturnValue(of(mockMovimiento)),
      update: jest.fn().mockReturnValue(of(mockMovimiento)),
    } as any;
    cuentaService = {
      getAll: jest.fn().mockReturnValue(of([mockCuenta])),
    } as any;
    notificationService = {
      showError: jest.fn(),
    } as any;

    component = new MovimientosFormComponent(new FormBuilder(), movimientoService, cuentaService, notificationService);
  });

  it('should create', () => expect(component).toBeTruthy());

  it('ngOnInit builds form and loads cuentas', () => {
    component.ngOnInit();
    expect(component.form).toBeTruthy();
    expect(component.cuentas).toHaveLength(1);
  });

  it('ngOnInit patches form when editing', () => {
    component.movimiento = mockMovimiento;
    component.ngOnInit();
    expect(component.form.value.tipoMovimiento).toBe('CREDITO');
  });

  it('isEditing false when no movimiento', () => {
    component.ngOnInit();
    expect(component.isEditing).toBe(false);
  });

  it('isEditing true when movimiento has id', () => {
    component.movimiento = mockMovimiento;
    component.ngOnInit();
    expect(component.isEditing).toBe(true);
  });

  it('onSubmit with invalid form marks all touched', () => {
    component.ngOnInit();
    const spy = jest.spyOn(component.form, 'markAllAsTouched');
    component.onSubmit();
    expect(spy).toHaveBeenCalled();
  });

  it('onSubmit creates new movimiento (CREDITO)', () => {
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({ tipoMovimiento: 'CREDITO', valor: 200, numeroCuenta: 225487 });
    component.onSubmit();
    expect(movimientoService.create).toHaveBeenCalledWith(expect.objectContaining({ valor: 200 }));
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit creates new movimiento (DEBITO)', () => {
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({ tipoMovimiento: 'DEBITO', valor: 100, numeroCuenta: 225487 });
    component.onSubmit();
    expect(movimientoService.create).toHaveBeenCalledWith(expect.objectContaining({ valor: -100 }));
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit updates existing movimiento', () => {
    component.movimiento = mockMovimiento;
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({ tipoMovimiento: 'CREDITO', valor: 300, numeroCuenta: 225487 });
    component.onSubmit();
    expect(movimientoService.update).toHaveBeenCalledWith(1, expect.anything());
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit handles error', () => {
    movimientoService.create.mockReturnValue(throwError(() => ({ error: { mensaje: 'Saldo insuf' } })));
    component.ngOnInit();
    component.form.patchValue({ tipoMovimiento: 'CREDITO', valor: 200, numeroCuenta: 225487 });
    component.onSubmit();
    expect(notificationService.showError).toHaveBeenCalledWith('Saldo insuf');
    expect(component.submitting).toBe(false);
  });

  it('onCancel emits cancelled', () => {
    const spy = jest.fn();
    component.cancelled.subscribe(spy);
    component.onCancel();
    expect(spy).toHaveBeenCalled();
  });

  it('f getter returns form controls', () => {
    component.ngOnInit();
    expect(component.f).toBe(component.form.controls);
  });
});
