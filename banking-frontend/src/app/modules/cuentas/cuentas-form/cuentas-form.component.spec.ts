import { FormBuilder } from '@angular/forms';
import { CuentasFormComponent } from './cuentas-form.component';
import { CuentaService } from '../../../core/services/cuenta.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { of, throwError } from 'rxjs';
import { Cuenta } from '../../../shared/models/cuenta.model';
import { Cliente } from '../../../shared/models/cliente.model';

const mockCuenta: Cuenta = {
  numeroCuenta: 225487,
  tipoCuenta: 'AHORRO',
  saldoInicial: 500,
  estado: true,
  clienteId: 'c1',
};

const mockCliente: Cliente = {
  clienteId: 'c1', nombre: 'Ana', genero: 'F', edad: 30,
  identificacion: '123456', direccion: 'Dir', telefono: '0991111111', estado: true,
};

describe('CuentasFormComponent', () => {
  let component: CuentasFormComponent;
  let cuentaService: jest.Mocked<CuentaService>;
  let clienteService: jest.Mocked<ClienteService>;

  beforeEach(() => {
    cuentaService = {
      create: jest.fn().mockReturnValue(of(mockCuenta)),
      patch: jest.fn().mockReturnValue(of(mockCuenta)),
    } as any;
    clienteService = {
      getAll: jest.fn().mockReturnValue(of([mockCliente])),
    } as any;

    component = new CuentasFormComponent(new FormBuilder(), cuentaService, clienteService);
  });

  it('should create', () => expect(component).toBeTruthy());

  it('ngOnInit builds form and loads clientes', () => {
    component.ngOnInit();
    expect(component.form).toBeTruthy();
    expect(component.clientes).toHaveLength(1);
  });

  it('isEditing false when no cuenta', () => {
    component.ngOnInit();
    expect(component.isEditing).toBe(false);
  });

  it('isEditing true when cuenta has numeroCuenta', () => {
    component.cuenta = mockCuenta;
    component.ngOnInit();
    expect(component.isEditing).toBe(true);
  });

  it('ngOnInit patches form with cuenta data', () => {
    component.cuenta = mockCuenta;
    component.ngOnInit();
    expect(component.form.value.numeroCuenta).toBe(225487);
  });

  it('onSubmit with invalid form marks all touched', () => {
    component.ngOnInit();
    const spy = jest.spyOn(component.form, 'markAllAsTouched');
    component.onSubmit();
    expect(spy).toHaveBeenCalled();
  });

  it('onSubmit creates new cuenta', () => {
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({ numeroCuenta: 111, tipoCuenta: 'AHORRO', saldoInicial: 100, estado: true, clienteId: 'c1' });
    component.onSubmit();
    expect(cuentaService.create).toHaveBeenCalled();
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit patches existing cuenta', () => {
    component.cuenta = mockCuenta;
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({ numeroCuenta: 225487, tipoCuenta: 'AHORRO', saldoInicial: 100, estado: true, clienteId: 'c1' });
    component.onSubmit();
    expect(cuentaService.patch).toHaveBeenCalled();
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit handles error', () => {
    cuentaService.create.mockReturnValue(throwError(() => new Error('err')));
    component.ngOnInit();
    component.form.patchValue({ numeroCuenta: 111, tipoCuenta: 'AHORRO', saldoInicial: 0, estado: true, clienteId: 'c1' });
    component.onSubmit();
    expect(component.submitting).toBe(false);
  });

  it('onCancel emits cancelled', () => {
    const spy = jest.fn();
    component.cancelled.subscribe(spy);
    component.onCancel();
    expect(spy).toHaveBeenCalled();
  });
});
