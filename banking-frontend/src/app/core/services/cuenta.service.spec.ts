import { CuentaService } from './cuenta.service';
import { of } from 'rxjs';
import { Cuenta } from '../../shared/models/cuenta.model';

describe('CuentaService', () => {
  let service: CuentaService;
  let httpSpy: {
    get: jest.Mock;
    post: jest.Mock;
    put: jest.Mock;
    delete: jest.Mock;
    patch: jest.Mock;
  };

  const mockCuenta: Cuenta = {
    numeroCuenta: 225487,
    tipoCuenta: 'AHORRO' as any,
    saldoInicial: 500,
    saldo: 500,
    estado: true,
    clienteId: 'abc123',
  };

  beforeEach(() => {
    httpSpy = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn(),
      patch: jest.fn(),
    };
    service = new CuentaService(httpSpy as any);
  });

  it('should call getAll and return cuentas', (done) => {
    httpSpy.get.mockReturnValue(of([mockCuenta]));
    service.getAll().subscribe((cuentas) => {
      expect(cuentas).toHaveLength(1);
      expect(cuentas[0].numeroCuenta).toBe(225487);
      done();
    });
    expect(httpSpy.get).toHaveBeenCalledWith(expect.stringContaining('/cuentas'));
  });

  it('should call getById with correct URL', (done) => {
    httpSpy.get.mockReturnValue(of(mockCuenta));
    service.getById(225487).subscribe((c) => {
      expect(c.numeroCuenta).toBe(225487);
      done();
    });
    expect(httpSpy.get).toHaveBeenCalledWith(expect.stringContaining('/cuentas/225487'));
  });

  it('should call create with POST', (done) => {
    httpSpy.post.mockReturnValue(of(mockCuenta));
    service.create(mockCuenta).subscribe((c) => {
      expect(c.numeroCuenta).toBe(225487);
      done();
    });
    expect(httpSpy.post).toHaveBeenCalledWith(expect.stringContaining('/cuentas'), mockCuenta);
  });

  it('should call update with PUT', (done) => {
    const updated = { ...mockCuenta, saldo: 600 };
    httpSpy.put.mockReturnValue(of(updated));
    service.update(225487, updated).subscribe((c) => {
      expect(c.saldo).toBe(600);
      done();
    });
    expect(httpSpy.put).toHaveBeenCalledWith(
      expect.stringContaining('/cuentas/225487'),
      updated
    );
  });

  it('should call patch with PATCH', (done) => {
    httpSpy.patch.mockReturnValue(of({ ...mockCuenta, estado: false }));
    service.patch(225487, { estado: false }).subscribe((c) => {
      expect(c.estado).toBe(false);
      done();
    });
    expect(httpSpy.patch).toHaveBeenCalledWith(
      expect.stringContaining('/cuentas/225487'),
      { estado: false }
    );
  });

  it('should call delete with DELETE', (done) => {
    httpSpy.delete.mockReturnValue(of(undefined));
    service.delete(225487).subscribe(() => done());
    expect(httpSpy.delete).toHaveBeenCalledWith(
      expect.stringContaining('/cuentas/225487')
    );
  });
});
