import { ReporteService } from './reporte.service';
import { of } from 'rxjs';
import { ReporteResponse } from '../../shared/models/reporte.model';

describe('ReporteService', () => {
  let service: ReporteService;
  let httpSpy: { get: jest.Mock };

  const mockResponse: ReporteResponse = {

    totalCreditos: 600,
    totalDebitos: 0,
    pdfBase64: 'base64pdf',
  } as any;

  beforeEach(() => {
    httpSpy = { get: jest.fn() };
    service = new ReporteService(httpSpy as any);
  });

  it('should call getReporte with correct params', (done) => {
    httpSpy.get.mockReturnValue(of(mockResponse));

    service.getReporte({ fechaInicio: '2022-01-01', fechaFin: '2022-12-31', cliente: 123 })
      .subscribe((r) => {
        expect(r.pdfBase64).toBe('base64pdf');
        done();
      });

    expect(httpSpy.get).toHaveBeenCalledWith(
      expect.stringContaining('/reportes'),
      expect.objectContaining({ params: expect.anything() })
    );
  });

  it('should include all query params', (done) => {
    httpSpy.get.mockReturnValue(of(mockResponse));

    service.getReporte({ fechaInicio: '2022-03-01', fechaFin: '2022-03-31', cliente: 456 })
      .subscribe(() => done());

    const callArgs = httpSpy.get.mock.calls[0];
    const params = callArgs[1].params;
    expect(params.get('fechaInicio')).toBe('2022-03-01');
    expect(params.get('fechaFin')).toBe('2022-03-31');
    expect(params.get('cliente')).toBe('456');
  });
});
