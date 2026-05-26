import { MovimientoService } from './movimiento.service';
import { Movimiento } from '../../shared/models/movimiento.model';
import { environment } from '../../../environments/environment';
import { of } from 'rxjs';

describe('MovimientoService', () => {
  let service: MovimientoService;
  let httpSpy: any;

  const mockMovimiento: Movimiento = {
    movimientoId: 1,
    fecha: '2022-10-02',
    tipoMovimiento: 'CREDITO',
    valor: 600,
    saldo: 700,
    numeroCuenta: 225487,
  };

  beforeEach(() => {
    httpSpy = { get: jest.fn(), post: jest.fn(), delete: jest.fn() };
    service = new MovimientoService(httpSpy as any);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getAll() should return movimientos via GET', () => {
    httpSpy.get.mockReturnValue(of([mockMovimiento]));

    service.getAll().subscribe(movimientos => {
      expect(movimientos.length).toBe(1);
      expect(movimientos[0].tipoMovimiento).toBe('CREDITO');
    });

    expect(httpSpy.get).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos`);
  });

  it('create() should POST a new movimiento — crédito increments saldo', () => {
    const newMov: Movimiento = { tipoMovimiento: 'CREDITO', valor: 600, numeroCuenta: 225487 } as any;
    httpSpy.post.mockReturnValue(of(mockMovimiento));

    service.create(newMov).subscribe(created => {
      expect(created.saldo).toBe(700);
      expect(created.tipoMovimiento).toBe('CREDITO');
    });

    expect(httpSpy.post).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos`, newMov);
  });

  it('create() DEBITO — backend should return positive valor (stored as negative)', () => {
    const debitoMov: Movimiento = { tipoMovimiento: 'DEBITO', valor: 540, numeroCuenta: 496825 } as any;
    const serverResponse: Movimiento = { ...debitoMov, movimientoId: 2, saldo: 0, valor: -540 } as any;
    httpSpy.post.mockReturnValue(of(serverResponse));

    service.create(debitoMov).subscribe(created => {
      expect(created.valor).toBe(-540);
      expect(created.saldo).toBe(0);
    });

    expect(httpSpy.post).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos`, debitoMov);
  });

  it('delete() should send DELETE to the correct URL', () => {
    httpSpy.delete.mockReturnValue(of(void 0));

    service.delete(1).subscribe(result => {
      expect(result).toBeUndefined();
    });

    expect(httpSpy.delete).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos/1`);
  });

  it('getById() should return a single movimiento', () => {
    httpSpy.get.mockReturnValue(of(mockMovimiento));

    service.getById(1).subscribe(m => {
      expect(m.movimientoId).toBe(1);
      expect(m.numeroCuenta).toBe(225487);
    });

    expect(httpSpy.get).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos/1`);
  });

  it('update() should PUT the movimiento to the correct URL', () => {
    httpSpy = { ...httpSpy, put: jest.fn() };
    service = new MovimientoService(httpSpy as any);
    httpSpy.put.mockReturnValue(of(mockMovimiento));

    service.update(1, mockMovimiento).subscribe(updated => {
      expect(updated.movimientoId).toBe(1);
    });

    expect(httpSpy.put).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos/1`, mockMovimiento);
  });

  it('patch() should PATCH a movimiento', () => {
    httpSpy = { ...httpSpy, patch: jest.fn() };
    service = new MovimientoService(httpSpy as any);
    const partial = { valor: 100 };
    httpSpy.patch.mockReturnValue(of({ ...mockMovimiento, valor: 100 }));

    service.patch(1, partial).subscribe(updated => {
      expect(updated.valor).toBe(100);
    });

    expect(httpSpy.patch).toHaveBeenCalledWith(`${environment.apiUrl}/movimientos/1`, partial);
  });
});
