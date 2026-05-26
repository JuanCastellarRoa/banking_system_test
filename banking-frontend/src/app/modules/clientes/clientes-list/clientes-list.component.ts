import { Component, OnInit } from '@angular/core';
import { Cliente } from '../../../shared/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  standalone: false,
  selector: 'app-clientes-list',
  templateUrl: './clientes-list.component.html',
  styleUrls: ['./clientes-list.component.scss'],
})
export class ClientesListComponent implements OnInit {
  clientes: Cliente[] = [];
  filteredClientes: Cliente[] = [];
  searchTerm = '';
  showForm = false;
  selectedCliente: Cliente | null = null;
  loading = false;

  constructor(
    private readonly clienteService: ClienteService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadClientes();
  }

  loadClientes(): void {
    this.loading = true;
    this.clienteService.getAll().subscribe({
      next: data => {
        this.clientes = data;
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
    this.filteredClientes = term
      ? this.clientes.filter(
          c =>
            c.nombre.toLowerCase().includes(term) ||
            c.identificacion.toLowerCase().includes(term) ||
            c.telefono.includes(term)
        )
      : [...this.clientes];
  }

  openCreate(): void {
    this.selectedCliente = null;
    this.showForm = true;
  }

  openEdit(cliente: Cliente): void {
    this.selectedCliente = { ...cliente };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.selectedCliente = null;
  }

  onSaved(): void {
    this.closeForm();
    this.loadClientes();
    this.notificationService.showSuccess('Cliente guardado correctamente');
  }

  delete(cliente: Cliente): void {
    if (!confirm(`¿Eliminar al cliente "${cliente.nombre}"?`)) return;
    this.clienteService.delete(cliente.clienteId!).subscribe({
      next: () => {
        this.notificationService.showSuccess('Cliente eliminado');
        this.loadClientes();
      },
      error: err =>
        this.notificationService.showError(
          err?.error?.mensaje ?? 'No se pudo eliminar el cliente'
        ),
    });
  }
}
