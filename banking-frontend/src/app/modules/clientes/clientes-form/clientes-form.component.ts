import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Cliente } from '../../../shared/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';

@Component({
  standalone: false,
  selector: 'app-clientes-form',
  templateUrl: './clientes-form.component.html',
  styleUrls: ['./clientes-form.component.scss'],
})
export class ClientesFormComponent implements OnInit {
  @Input() cliente: Cliente | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  form!: FormGroup;
  submitting = false;

  constructor(
    private readonly fb: FormBuilder,
    private readonly clienteService: ClienteService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    if (this.cliente) this.form.patchValue(this.cliente);
  }

  get isEditing(): boolean {
    return !!this.cliente?.clienteId;
  }

  get f() {
    return this.form.controls;
  }

  private buildForm(): void {
    this.form = this.fb.group({
      nombre:        ['', [Validators.required, Validators.minLength(3)]],
      genero:        ['', Validators.required],
      edad:          [null, [Validators.required, Validators.min(1), Validators.max(120)]],
      identificacion:['', [Validators.required, Validators.minLength(6)]],
      direccion:     ['', Validators.required],
      telefono:      ['', [Validators.required, Validators.pattern(/^\d{7,15}$/)]],
      contrasena:    ['', this.isEditing ? [] : [Validators.required, Validators.minLength(4)]],
      estado:        [true, Validators.required],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting = true;
    const raw = this.form.value;

    const op$ = this.isEditing
      ? this.clienteService.patch(this.cliente!.clienteId!, {
          ...raw,
          contrasena: raw.contrasena?.trim() ? raw.contrasena : null,
        })
      : this.clienteService.create(raw as Cliente);

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
