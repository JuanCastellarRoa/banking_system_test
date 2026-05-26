import { ClienteService } from './cliente.service';
import { Cliente } from '../../shared/models/cliente.model';
import { environment } from '../../../environments/environment';
import { of } from 'rxjs';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpSpy: any;

  const mockCliente: Cliente = {
    clienteId: '1',
    nombre: 'Jose Lema',
    genero: 'M',
    edad: 35,
    identificacion: '1234567890',
    direccion: 'Otavalo sn y principal',
    telefono: '098254785',
    estado: true,
  };

  beforeEach(() => {
    httpSpy = { get: jest.fn(), post: jest.fn(), put: jest.fn(), delete: jest.fn(), patch: jest.fn() };
    service = new ClienteService(httpSpy as any);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() should return a list of clientes via GET', () => {
    const mockList: Cliente[] = [mockCliente];
    httpSpy.get.mockReturnValue(of(mockList));

    service.getAll().subscribe(clientes => {
      expect(clientes.length).toBe(1);
      expect(clientes[0].nombre).toBe('Jose Lema');
    });

    expect(httpSpy.get).toHaveBeenCalledWith(`${environment.apiUrl}/clientes`);
  });

  it('create() should POST the cliente and return the created record', () => {
    const newCliente: Cliente = { ...mockCliente, clienteId: undefined };
    httpSpy.post.mockReturnValue(of(mockCliente));

    service.create(newCliente).subscribe(created => {
      expect(created.clienteId).toBe('1');
      expect(created.nombre).toBe('Jose Lema');
    });

    expect(httpSpy.post).toHaveBeenCalledWith(`${environment.apiUrl}/clientes`, newCliente);
  });

  it('update() should PUT the cliente to the correct URL', () => {
    httpSpy.put.mockReturnValue(of(mockCliente));

    service.update('1', mockCliente).subscribe(updated => {
      expect(updated.clienteId).toBe('1');
    });

    expect(httpSpy.put).toHaveBeenCalledWith(`${environment.apiUrl}/clientes/1`, mockCliente);
  });

  it('delete() should send DELETE to the correct URL', () => {
    httpSpy.delete.mockReturnValue(of(void 0));

    service.delete('1').subscribe(result => {
      expect(result).toBeUndefined();
    });

    expect(httpSpy.delete).toHaveBeenCalledWith(`${environment.apiUrl}/clientes/1`);
  });

  it('patch() should send PATCH with partial data', () => {
    const patch: Partial<Cliente> = { estado: false };
    httpSpy.patch.mockReturnValue(of({ ...mockCliente, estado: false }));

    service.patch('1', patch).subscribe(updated => {
      expect(updated.estado).toBe(false);
    });

    expect(httpSpy.patch).toHaveBeenCalledWith(`${environment.apiUrl}/clientes/1`, patch);
  });

  it('getById() should return a single cliente via GET', () => {
    httpSpy.get.mockReturnValue(of(mockCliente));

    service.getById('1').subscribe(c => {
      expect(c.clienteId).toBe('1');
      expect(c.nombre).toBe('Jose Lema');
    });

    expect(httpSpy.get).toHaveBeenCalledWith(`${environment.apiUrl}/clientes/1`);
  });
});
