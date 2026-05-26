import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReporteService } from '../../../core/services/reporte.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { ReporteLinea } from '../../../shared/models/reporte.model';
import { Cliente } from '../../../shared/models/cliente.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  standalone: false,
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.scss'],
})
export class ReportesComponent implements OnInit {
  form!: FormGroup;
  clientes: Cliente[] = [];
  reporteLineas: ReporteLinea[] = [];
  totalDebitos = 0;
  totalCreditos = 0;
  pdfBase64 = '';
  loading = false;
  searched = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly reporteService: ReporteService,
    private readonly clienteService: ClienteService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadClientes();
  }

  get f() {
    return this.form.controls;
  }

  private buildForm(): void {
    this.form = this.fb.group({
      fechaInicio: ['', Validators.required],
      fechaFin:    ['', Validators.required],
      cliente:     [null, Validators.required],
    });
  }

  private loadClientes(): void {
    this.clienteService.getAll().subscribe(data => (this.clientes = data));
  }

  onConsultar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.searched = true;

    this.reporteService.getReporte(this.form.value).subscribe({
      next: response => {
        this.reporteLineas = response.movimientos ?? [];
        this.totalDebitos = response.totalDebitos ?? 0;
        this.totalCreditos = response.totalCreditos ?? 0;
        this.pdfBase64 = response.pdfBase64 ?? '';
        this.loading = false;

        if (this.reporteLineas.length === 0) {
          this.notificationService.showWarning('No se encontraron movimientos en el rango seleccionado.');
        }
      },
      error: () => (this.loading = false),
    });
  }

  downloadPdf(): void {
    if (!this.pdfBase64) {
      this.notificationService.showWarning('No hay PDF disponible para descargar.');
      return;
    }
    const byteCharacters = atob(this.pdfBase64);
    const byteNumbers = new Array(byteCharacters.length)
      .fill(null)
      .map((_, i) => byteCharacters.charCodeAt(i));
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: 'application/pdf' });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `reporte_${this.form.value.fechaInicio}_${this.form.value.fechaFin}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
