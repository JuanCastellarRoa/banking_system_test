import { Component, OnInit } from '@angular/core';
import { Movimiento } from '../../../shared/models/movimiento.model';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  standalone: false,
  selector: 'app-movimientos-list',
  templateUrl: './movimientos-list.component.html',
  styleUrls: ['./movimientos-list.component.scss'],
})
export class MovimientosListComponent implements OnInit {
  movimientos: Movimiento[] = [];
  filtered: Movimiento[] = [];
  searchTerm = '';
  showForm = false;
  selected: Movimiento | null = null;
  loading = false;

  constructor(
    private readonly movimientoService: MovimientoService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.movimientoService.getAll().subscribe({
      next: data => {
        this.movimientos = data;
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
    this.filtered = term
      ? this.movimientos.filter(
          m =>
            m.numeroCuenta?.toString().includes(term) ||
            m.tipoMovimiento.toLowerCase().includes(term)
        )
      : [...this.movimientos];
  }

  openCreate(): void {
    this.selected = null;
    this.showForm = true;
  }

  openEdit(m: Movimiento): void {
    this.selected = { ...m };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selected = null;
  }

  onSaved(): void {
    this.closeForm();
    this.load();
    this.notificationService.showSuccess('Movimiento guardado correctamente');
  }

  delete(m: Movimiento): void {
    if (!confirm(`¿Eliminar movimiento #${m.movimientoId}?`)) return;
    this.movimientoService.delete(m.movimientoId!).subscribe({
      next: () => {
        this.notificationService.showSuccess('Movimiento eliminado');
        this.load();
      },
      error: err =>
        this.notificationService.showError(
          err?.error?.mensaje ?? 'No se pudo eliminar el movimiento'
        ),
    });
  }
}
