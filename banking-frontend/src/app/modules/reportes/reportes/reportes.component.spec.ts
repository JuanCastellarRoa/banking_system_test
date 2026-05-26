import { FormBuilder } from '@angular/forms';
import { ReportesComponent } from './reportes.component';
import { ReporteService } from '../../../core/services/reporte.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';
import { of, throwError } from 'rxjs';
import { Cliente } from '../../../shared/models/cliente.model';
import { ReporteResponse } from '../../../shared/models/reporte.model';

const mockCliente: Cliente = {
  clienteId: 'c1', nombre: 'Ana', genero: 'F', edad: 30,
  identificacion: '123456', direccion: 'Dir', telefono: '0991111111', estado: true,
};

const mockResponse: ReporteResponse = {
  movimientos: [
    { fecha: '2022-01-01', cliente: 'Ana', numeroCuenta: 225487, tipo: 'CREDITO', saldoInicial: 500, estado: true, movimiento: 100, saldoDisponible: 600 }
  ],
  totalDebitos: 0,
  totalCreditos: 100,
  pdfBase64: btoa('%PDF-test'),
};

describe('ReportesComponent', () => {
  let component: ReportesComponent;
  let reporteService: jest.Mocked<ReporteService>;
  let clienteService: jest.Mocked<ClienteService>;
  let notificationService: jest.Mocked<NotificationService>;

  beforeEach(() => {
    reporteService = {
      getReporte: jest.fn().mockReturnValue(of(mockResponse)),
    } as any;
    clienteService = {
      getAll: jest.fn().mockReturnValue(of([mockCliente])),
    } as any;
    notificationService = {
      showWarning: jest.fn(),
      showError: jest.fn(),
    } as any;

    component = new ReportesComponent(new FormBuilder(), reporteService, clienteService, notificationService);
  });

  it('should create', () => expect(component).toBeTruthy());

  it('ngOnInit builds form and loads clientes', () => {
    component.ngOnInit();
    expect(component.form).toBeTruthy();
    expect(component.clientes).toHaveLength(1);
  });

  it('f getter returns form controls', () => {
    component.ngOnInit();
    expect(component.f).toBe(component.form.controls);
  });

  it('onConsultar with invalid form marks all touched', () => {
    component.ngOnInit();
    const spy = jest.spyOn(component.form, 'markAllAsTouched');
    component.onConsultar();
    expect(spy).toHaveBeenCalled();
  });

  it('onConsultar with valid form fetches reporte', () => {
    component.ngOnInit();
    component.form.patchValue({ fechaInicio: '2022-01-01', fechaFin: '2022-12-31', cliente: 'c1' });
    component.onConsultar();
    expect(reporteService.getReporte).toHaveBeenCalled();
    expect(component.reporteLineas).toHaveLength(1);
    expect(component.totalCreditos).toBe(100);
    expect(component.loading).toBe(false);
  });

  it('onConsultar shows warning when no movimientos', () => {
    reporteService.getReporte.mockReturnValue(of({ movimientos: [], totalDebitos: 0, totalCreditos: 0, pdfBase64: '' }));
    component.ngOnInit();
    component.form.patchValue({ fechaInicio: '2022-01-01', fechaFin: '2022-12-31', cliente: 'c1' });
    component.onConsultar();
    expect(notificationService.showWarning).toHaveBeenCalled();
  });

  it('onConsultar handles error', () => {
    reporteService.getReporte.mockReturnValue(throwError(() => new Error('err')));
    component.ngOnInit();
    component.form.patchValue({ fechaInicio: '2022-01-01', fechaFin: '2022-12-31', cliente: 'c1' });
    component.onConsultar();
    expect(component.loading).toBe(false);
  });

  it('downloadPdf shows warning when no pdfBase64', () => {
    component.ngOnInit();
    component.pdfBase64 = '';
    component.downloadPdf();
    expect(notificationService.showWarning).toHaveBeenCalledWith('No hay PDF disponible para descargar.');
  });

  it('downloadPdf creates download link when pdfBase64 present', () => {
    component.ngOnInit();
    component.form.patchValue({ fechaInicio: '2022-01-01', fechaFin: '2022-12-31', cliente: 'c1' });
    component.pdfBase64 = btoa('%PDF-test');

    const mockAnchor = { href: '', download: '', click: jest.fn() };
    jest.spyOn(document, 'createElement').mockReturnValue(mockAnchor as any);

    // jsdom may not have URL.createObjectURL — define it if missing
    const originalCreate = URL.createObjectURL;
    const originalRevoke = URL.revokeObjectURL;
    URL.createObjectURL = jest.fn().mockReturnValue('blob:url');
    URL.revokeObjectURL = jest.fn();

    component.downloadPdf();

    expect(mockAnchor.click).toHaveBeenCalled();
    expect(URL.revokeObjectURL).toHaveBeenCalled();

    URL.createObjectURL = originalCreate;
    URL.revokeObjectURL = originalRevoke;
  });

  it('onConsultar handles response with null fields (null coalescing branches)', () => {
    reporteService.getReporte.mockReturnValue(
      of({ movimientos: null as any, totalDebitos: null as any, totalCreditos: null as any, pdfBase64: null as any })
    );
    component.ngOnInit();
    component.form.patchValue({ fechaInicio: '2022-01-01', fechaFin: '2022-12-31', cliente: 'c1' });
    component.onConsultar();
    expect(component.reporteLineas).toEqual([]);
    expect(component.totalDebitos).toBe(0);
    expect(component.totalCreditos).toBe(0);
    expect(component.pdfBase64).toBe('');
    expect(notificationService.showWarning).toHaveBeenCalled();
  });
});
