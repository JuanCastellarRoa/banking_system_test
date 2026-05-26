import { ClientesListComponent } from './clientes-list.component';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';
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

describe('ClientesListComponent', () => {
  let component: ClientesListComponent;
  let clienteService: jest.Mocked<ClienteService>;
  let notificationService: jest.Mocked<NotificationService>;

  beforeEach(() => {
    clienteService = {
      getAll: jest.fn().mockReturnValue(of([mockCliente])),
      delete: jest.fn().mockReturnValue(of(undefined)),
    } as any;

    notificationService = {
      showSuccess: jest.fn(),
      showError: jest.fn(),
    } as any;

    component = new ClientesListComponent(clienteService, notificationService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit loads clientes', () => {
    component.ngOnInit();
    expect(clienteService.getAll).toHaveBeenCalled();
    expect(component.clientes).toHaveLength(1);
    expect(component.loading).toBe(false);
  });

  it('loadClientes handles error', () => {
    clienteService.getAll.mockReturnValue(throwError(() => new Error('error')));
    component.loadClientes();
    expect(component.loading).toBe(false);
  });

  it('onSearch filters clientes by name', () => {
    component.ngOnInit();
    const event = { target: { value: 'ana' } } as any;
    component.onSearch(event);
    expect(component.filteredClientes).toHaveLength(1);
  });

  it('onSearch with empty term shows all', () => {
    component.ngOnInit();
    const event = { target: { value: '' } } as any;
    component.onSearch(event);
    expect(component.filteredClientes).toHaveLength(1);
  });

  it('openCreate sets showForm true and selectedCliente null', () => {
    component.openCreate();
    expect(component.showForm).toBe(true);
    expect(component.selectedCliente).toBeNull();
  });

  it('openEdit sets selectedCliente and showForm true', () => {
    component.openEdit(mockCliente);
    expect(component.showForm).toBe(true);
    expect(component.selectedCliente).toEqual(mockCliente);
  });

  it('closeForm hides form', () => {
    component.showForm = true;
    component.closeForm();
    expect(component.showForm).toBe(false);
  });

  it('onSaved closes form and reloads', () => {
    component.ngOnInit();
    component.onSaved();
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Cliente guardado correctamente');
  });

  it('delete calls service and shows success', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    component.ngOnInit();
    component.delete(mockCliente);
    expect(clienteService.delete).toHaveBeenCalledWith('c1');
    expect(notificationService.showSuccess).toHaveBeenCalledWith('Cliente eliminado');
  });

  it('delete shows error on failure', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    clienteService.delete.mockReturnValue(throwError(() => ({ error: { mensaje: 'Error' } })));
    component.ngOnInit();
    component.delete(mockCliente);
    expect(notificationService.showError).toHaveBeenCalledWith('Error');
  });

  it('delete does nothing if confirm is false', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(false);
    component.delete(mockCliente);
    expect(clienteService.delete).not.toHaveBeenCalled();
  });

  it('onSearch filters clientes by identificacion when nombre does not match', () => {
    component.ngOnInit();
    const event = { target: { value: '1234567890' } } as any;
    component.onSearch(event);
    expect(component.filteredClientes).toHaveLength(1);
  });

  it('onSearch filters clientes by telefono when nombre and identificacion do not match', () => {
    component.ngOnInit();
    const event = { target: { value: '0991234567' } } as any;
    component.onSearch(event);
    expect(component.filteredClientes).toHaveLength(1);
  });

  it('onSearch returns empty when no clientes match the term', () => {
    component.ngOnInit();
    const event = { target: { value: 'zzznomatch' } } as any;
    component.onSearch(event);
    expect(component.filteredClientes).toHaveLength(0);
  });

  it('delete shows default error when err.error.mensaje is absent', () => {
    jest.spyOn(global, 'confirm').mockReturnValue(true);
    clienteService.delete.mockReturnValue(throwError(() => ({ error: {} })));
    component.ngOnInit();
    component.delete(mockCliente);
    expect(notificationService.showError).toHaveBeenCalledWith('No se pudo eliminar el cliente');
  });
});
