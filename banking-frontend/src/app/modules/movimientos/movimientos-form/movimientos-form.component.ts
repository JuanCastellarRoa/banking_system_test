import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Movimiento } from '../../../shared/models/movimiento.model';
import { MovimientoService } from '../../../core/services/movimiento.service';
import { CuentaService } from '../../../core/services/cuenta.service';
import { NotificationService } from '../../../core/services/notification.service';
import { Cuenta } from '../../../shared/models/cuenta.model';

@Component({
  standalone: false,
  selector: 'app-movimientos-form',
  templateUrl: './movimientos-form.component.html',
  styleUrls: ['./movimientos-form.component.scss'],
})
export class MovimientosFormComponent implements OnInit {
  @Input() movimiento: Movimiento | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  form!: FormGroup;
  cuentas: Cuenta[] = [];
  submitting = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly movimientoService: MovimientoService,
    private readonly cuentaService: CuentaService,
    private readonly notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadCuentas();
    if (this.movimiento) {
      this.form.patchValue({
        ...this.movimiento,
        valor: Math.abs(this.movimiento.valor),
      });
    }
  }

  get isEditing(): boolean {
    return !!this.movimiento?.movimientoId;
  }

  get f() {
    return this.form.controls;
  }

  private buildForm(): void {
    this.form = this.fb.group({
      tipoMovimiento: ['', Validators.required],
      valor: [null, [Validators.required, Validators.min(0.01)]],
      numeroCuenta: [null, Validators.required],
    });
  }

  private loadCuentas(): void {
    this.cuentaService.getAll().subscribe(data => (this.cuentas = data));
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting = true;
    const raw = this.form.value;
    // El backend usa el signo del valor para determinar DEBITO/CREDITO
    const valorFinal: number =
      raw.tipoMovimiento === 'DEBITO' ? -Math.abs(raw.valor) : Math.abs(raw.valor);
    const payload = { numeroCuenta: raw.numeroCuenta, valor: valorFinal } as Movimiento;

    const op$ = this.isEditing
      ? this.movimientoService.update(this.movimiento!.movimientoId!, payload)
      : this.movimientoService.create(payload);

    op$.subscribe({
      next: () => {
        this.submitting = false;
        this.saved.emit();
      },
      error: err => {
        this.submitting = false;
        this.notificationService.showError(
          err?.error?.mensaje ?? 'Error al registrar el movimiento'
        );
      },
    });
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}
