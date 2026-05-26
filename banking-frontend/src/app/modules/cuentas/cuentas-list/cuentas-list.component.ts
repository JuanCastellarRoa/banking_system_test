import { Component, OnInit } from '@angular/core';
import { Cuenta } from '../../../shared/models/cuenta.model';
import { CuentaService } from '../../../core/services/cuenta.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  standalone: false,
  selector: 'app-cuentas-list',
  templateUrl: './cuentas-list.component.html',
  styleUrls: ['./cuentas-list.component.scss'],
})
export class CuentasListComponent implements OnInit {
  cuentas: Cuenta[] = [];
  filteredCuentas: Cuenta[] = [];
  searchTerm = '';
  showForm = false;
  selectedCuenta: Cuenta | null = null;
  loading = false;

  constructor(
    private readonly cuentaService: CuentaService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadCuentas();
  }

  loadCuentas(): void {
    this.loading = true;
    this.cuentaService.getAll().subscribe({
      next: data => {
        this.cuentas = data;
        this.applyFilter();
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.applyFilter();
  }

  private applyFilter(): void {
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredCuentas = term
      ? this.cuentas.filter(
          c =>
            c.numeroCuenta?.toString().includes(term) ||
            c.tipoCuenta.toLowerCase().includes(term) ||
            (c.clienteNombre ?? '').toLowerCase().includes(term)
        )
      : [...this.cuentas];
  }

  openCreate(): void {
    this.selectedCuenta = null;
    this.showForm = true;
  }

  openEdit(cuenta: Cuenta): void {
    this.selectedCuenta = { ...cuenta };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selectedCuenta = null;
  }

  onSaved(): void {
    this.closeForm();
    this.loadCuentas();
    this.notificationService.showSuccess('Cuenta guardada correctamente');
  }

  delete(cuenta: Cuenta): void {
    if (!confirm(`¿Eliminar la cuenta "${cuenta.numeroCuenta}"?`)) return;
    this.cuentaService.delete(cuenta.numeroCuenta!).subscribe({
      next: () => {
        this.notificationService.showSuccess('Cuenta eliminada');
        this.loadCuentas();
      },
      error: err =>
        this.notificationService.showError(
          err?.error?.mensaje ?? 'No se pudo eliminar la cuenta'
        ),
    });
  }
}
