import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MovimientoService } from './movimiento.service';
import { Movimiento } from '../../shared/models/movimiento.model';
import { environment } from '../../../environments/environment';

describe('MovimientoService', () => {
  let service: MovimientoService;
  let httpMock: HttpTestingController;

  const mockMovimiento: Movimiento = {
    id: 1,
    fecha: '2022-10-02',
    tipoMovimiento: 'CREDITO',
    valor: 600,
    saldo: 700,
    cuentaId: 2,
    numeroCuenta: '225487',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MovimientoService],
    });
    service = TestBed.inject(MovimientoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() should return movimientos via GET', () => {
    service.getAll().subscribe(movimientos => {
      expect(movimientos.length).toBe(1);
      expect(movimientos[0].tipoMovimiento).toBe('CREDITO');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/movimientos`);
    expect(req.request.method).toBe('GET');
    req.flush([mockMovimiento]);
  });

  it('create() should POST a new movimiento — crédito increments saldo', () => {
    const newMov: Movimiento = { tipoMovimiento: 'CREDITO', valor: 600, cuentaId: 2 };

    service.create(newMov).subscribe(created => {
      expect(created.saldo).toBe(700);
      expect(created.tipoMovimiento).toBe('CREDITO');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/movimientos`);
    expect(req.request.method).toBe('POST');
    req.flush(mockMovimiento);
  });

  it('create() DEBITO — backend should return positive valor (stored as negative)', () => {
    const debitoMov: Movimiento = { tipoMovimiento: 'DEBITO', valor: 540, cuentaId: 4 };
    const serverResponse: Movimiento = { ...debitoMov, id: 2, saldo: 0, valor: -540 };

    service.create(debitoMov).subscribe(created => {
      expect(created.valor).toBe(-540);
      expect(created.saldo).toBe(0);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/movimientos`);
    expect(req.request.method).toBe('POST');
    req.flush(serverResponse);
  });

  it('delete() should send DELETE to the correct URL', () => {
    service.delete(1).subscribe(result => {
      expect(result).toBeUndefined();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/movimientos/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('getById() should return a single movimiento', () => {
    service.getById(1).subscribe(m => {
      expect(m.id).toBe(1);
      expect(m.numeroCuenta).toBe('225487');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/movimientos/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockMovimiento);
  });
});
