import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Cuenta } from '../../../shared/models/cuenta.model';
import { CuentaService } from '../../../core/services/cuenta.service';
import { ClienteService } from '../../../core/services/cliente.service';
import { Cliente } from '../../../shared/models/cliente.model';

@Component({
  standalone: false,
  selector: 'app-cuentas-form',
  templateUrl: './cuentas-form.component.html',
  styleUrls: ['./cuentas-form.component.scss'],
})
export class CuentasFormComponent implements OnInit {
  @Input() cuenta: Cuenta | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  form!: FormGroup;
  clientes: Cliente[] = [];
  submitting = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly cuentaService: CuentaService,
    private readonly clienteService: ClienteService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadClientes();
    if (this.cuenta) this.form.patchValue(this.cuenta);
  }

  get isEditing(): boolean {
    return !!this.cuenta?.numeroCuenta;
  }

  get f() {
    return this.form.controls;
  }

  private buildForm(): void {
    this.form = this.fb.group({
      numeroCuenta: [null, [Validators.required, Validators.min(1)]],
      tipoCuenta:   ['', Validators.required],
      saldoInicial: [0, [Validators.required, Validators.min(0)]],
      estado:       [true, Validators.required],
      clienteId:    [null, Validators.required],
    });
  }

  private loadClientes(): void {
    this.clienteService.getAll().subscribe(data => (this.clientes = data));
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting = true;
    const raw = this.form.value;

    const op$ = this.isEditing
      ? this.cuentaService.patch(this.cuenta!.numeroCuenta!, raw)
      : this.cuentaService.create(raw as Cuenta);

    op$.subscribe({
      next: () => {
        this.submitting = false;
        this.saved.emit();
      },
      error: () => (this.submitting = false),
    });
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
