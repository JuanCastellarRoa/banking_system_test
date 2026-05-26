import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService } from './cliente.service';
import { Cliente } from '../../shared/models/cliente.model';
import { environment } from '../../../environments/environment';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;

  const mockCliente: Cliente = {
    clienteid: 1,
    nombre: 'Jose Lema',
    genero: 'M',
    edad: 35,
    identificacion: '1234567890',
    direccion: 'Otavalo sn y principal',
    telefono: '098254785',
    estado: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService],
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() should return a list of clientes via GET', () => {
    const mockList: Cliente[] = [mockCliente];

    service.getAll().subscribe(clientes => {
      expect(clientes.length).toBe(1);
      expect(clientes[0].nombre).toBe('Jose Lema');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clientes`);
    expect(req.request.method).toBe('GET');
    req.flush(mockList);
  });

  it('create() should POST the cliente and return the created record', () => {
    const newCliente: Cliente = { ...mockCliente, clienteid: undefined };

    service.create(newCliente).subscribe(created => {
      expect(created.clienteid).toBe(1);
      expect(created.nombre).toBe('Jose Lema');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clientes`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newCliente);
    req.flush(mockCliente);
  });

  it('update() should PUT the cliente to the correct URL', () => {
    service.update(1, mockCliente).subscribe(updated => {
      expect(updated.clienteid).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clientes/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockCliente);
  });

  it('delete() should send DELETE to the correct URL', () => {
    service.delete(1).subscribe(result => {
      expect(result).toBeUndefined();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clientes/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('patch() should send PATCH with partial data', () => {
    const patch: Partial<Cliente> = { estado: false };

    service.patch(1, patch).subscribe(updated => {
      expect(updated.estado).toBe(false);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/clientes/1`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual(patch);
    req.flush({ ...mockCliente, estado: false });
  });
});
