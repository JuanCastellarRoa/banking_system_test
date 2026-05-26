export type TipoCuenta = 'AHORRO' | 'CORRIENTE';

export interface Cuenta {
  numeroCuenta?: number;
  tipoCuenta: TipoCuenta;
  saldoInicial: number;
  saldo?: number;
  estado: boolean;
  clienteId?: string;
  clienteNombre?: string;
}
