export type TipoMovimiento = 'CREDITO' | 'DEBITO';

export interface Movimiento {
  movimientoId?: number;
  fecha?: string;
  tipoMovimiento: TipoMovimiento;
  valor: number;
  saldo?: number;
  numeroCuenta?: number;
}
