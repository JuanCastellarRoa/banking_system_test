import { FormBuilder } from '@angular/forms';
import { ClientesFormComponent } from './clientes-form.component';
import { ClienteService } from '../../../core/services/cliente.service';
import { of, throwError } from 'rxjs';
import { Cliente } from '../../../shared/models/cliente.model';

const mockCliente: Cliente = {
  clienteId: 'c1',
  nombre: 'Ana López',
  genero: 'F',
  edad: 30,
  identificacion: '1234567890',
  direccion: 'Calle 1',
  telefono: '0991234567',
  estado: true,
};

describe('ClientesFormComponent', () => {
  let component: ClientesFormComponent;
  let clienteService: jest.Mocked<ClienteService>;

  beforeEach(() => {
    clienteService = {
      create: jest.fn().mockReturnValue(of(mockCliente)),
      patch: jest.fn().mockReturnValue(of(mockCliente)),
    } as any;

    component = new ClientesFormComponent(new FormBuilder(), clienteService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit builds form', () => {
    component.ngOnInit();
    expect(component.form).toBeTruthy();
  });

  it('isEditing returns false when no cliente', () => {
    component.ngOnInit();
    expect(component.isEditing).toBe(false);
  });

  it('isEditing returns true when cliente has clienteId', () => {
    component.cliente = mockCliente;
    component.ngOnInit();
    expect(component.isEditing).toBe(true);
  });

  it('ngOnInit patches form with cliente data', () => {
    component.cliente = mockCliente;
    component.ngOnInit();
    expect(component.form.value.nombre).toBe('Ana López');
  });

  it('onSubmit with invalid form marks all touched', () => {
    component.ngOnInit();
    const spy = jest.spyOn(component.form, 'markAllAsTouched');
    component.onSubmit();
    expect(spy).toHaveBeenCalled();
  });

  it('onSubmit creates new cliente', () => {
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({
      nombre: 'Test User',
      genero: 'M',
      edad: 25,
      identificacion: '1234567',
      direccion: 'Calle 1',
      telefono: '09912345678',
      contrasena: 'pass123',
      estado: true,
    });
    component.onSubmit();
    expect(clienteService.create).toHaveBeenCalled();
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit patches existing cliente', () => {
    component.cliente = mockCliente;
    component.ngOnInit();
    const savedSpy = jest.fn();
    component.saved.subscribe(savedSpy);
    component.form.patchValue({
      nombre: 'Updated',
      genero: 'F',
      edad: 31,
      identificacion: '1234567890',
      direccion: 'Calle 2',
      telefono: '0991234567',
      contrasena: '',
      estado: true,
    });
    component.onSubmit();
    expect(clienteService.patch).toHaveBeenCalled();
    expect(savedSpy).toHaveBeenCalled();
  });

  it('onSubmit handles error', () => {
    clienteService.create.mockReturnValue(throwError(() => new Error('fail')));
    component.ngOnInit();
    component.form.patchValue({
      nombre: 'Test', genero: 'M', edad: 25, identificacion: '1234567',
      direccion: 'Dir', telefono: '09912345678', contrasena: 'pass', estado: true,
    });
    component.onSubmit();
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
