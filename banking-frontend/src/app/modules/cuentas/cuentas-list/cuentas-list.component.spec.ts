import { CuentasListComponent } from './cuentas-list.component';
import { CuentaService } from '../../../core/services/cuenta.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of, throwError } from 'rxjs';
import { Cuenta } from '../../../shared/models/cuenta.model';

const mockCuenta: Cuenta = {
  numeroCuenta: 225487,
  tipoCuenta: 'AHORRO',
  saldoInicial: 500,
  saldo: 500,
  estado: true,
  clienteId: 'c1',
  clienteNombre: 'Ana López',
};

describe('CuentasListComponent', () => {
  let component: CuentasListComponent;
  let cuentaService: jest.Mocked<CuentaService>;
  let notificationService: jest.Mocked<NotificationService>;

  beforeEach(() => {
    cuentaService = {
      getAll: jest.fn().mockReturnValue(of([mockCuenta])),
      delete: jest.fn().mockReturnValue(of(undefined)),
    } as any;

    notificationService = {
      showSuccess: jest.fn(),
      showError: jest.fn(),
    } as any;

    component = new CuentasListComponent(cuentaService, notificationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit loads cuentas', () => {
    component.ngOnInit();
    expect(cuentaService.getAll).toHaveBeenCalled();
    expect(component.cuentas).toHaveLength(1);
    expect(component.loading).toBe(false);
  });

  it('loadCuentas handles error', () => {
    cuentaService.getAll.mockReturnValue(throwError(() => new Error('err')));
    component.loadCuentas();
    expect(component.loading).toBe(false);
  });

  it('onSearch filters by numeroCuenta', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: '225487' } } as any);
    expect(component.filteredCuentas).toHaveLength(1);
  });

  it('onSearch with empty term shows all', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: '' } } as any);
    expect(component.filteredCuentas).toHaveLength(1);
  });

  it('openCreate sets showForm and clears selection', () => {
    component.openCreate();
    expect(component.showForm).toBe(true);
    expect(component.selectedCuenta).toBeNull();
  });

  it('openEdit sets selectedCuenta', () => {
    component.openEdit(mockCuenta);
    expect(component.selectedCuenta).toEqual(mockCuenta);
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
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Cuenta guardada correctamente');
  });

  it('delete calls service on confirm', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    component.ngOnInit();
    component.delete(mockCuenta);
    expect(cuentaService.delete).toHaveBeenCalledWith(225487);
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Cuenta eliminada');
  });

  it('delete shows error on failure', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    cuentaService.delete.mockReturnValue(throwError(() => ({ error: { mensaje: 'Error cuenta' } })));
    component.ngOnInit();
    component.delete(mockCuenta);
    expect(notificationService.showError).toHaveBeenCalledWith('Error cuenta');
  });

  it('delete does nothing if cancelled', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(false);
    component.delete(mockCuenta);
    expect(cuentaService.delete).not.toHaveBeenCalled();
  });

  it('onSearch filters cuentas by tipoCuenta', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: 'ahorro' } } as any);
    expect(component.filteredCuentas).toHaveLength(1);
  });

  it('onSearch filters cuentas by clienteNombre', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: 'ana' } } as any);
    expect(component.filteredCuentas).toHaveLength(1);
  });

  it('onSearch handles cuenta without clienteNombre (null coalescing branch)', () => {
    const cuentaSinNombre: Cuenta = { ...mockCuenta, clienteNombre: undefined };
    cuentaService.getAll.mockReturnValue(of([cuentaSinNombre]));
    component.ngOnInit();
    component.onSearch({ target: { value: 'nomatch' } } as any);
    expect(component.filteredCuentas).toHaveLength(0);
  });

  it('onSearch returns empty when no cuentas match', () => {
    component.ngOnInit();
    component.onSearch({ target: { value: 'zzznomatch' } } as any);
    expect(component.filteredCuentas).toHaveLength(0);
  });

  it('delete shows default error when err.error.mensaje is absent', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    cuentaService.delete.mockReturnValue(throwError(() => ({ error: {} })));
    component.ngOnInit();
    component.delete(mockCuenta);
    expect(notificationService.showError).toHaveBeenCalledWith('No se pudo eliminar la cuenta');
  });
});
