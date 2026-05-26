export interface ReporteLinea {
  fecha: string;
  cliente: string;
  numeroCuenta: number;
  tipo: string;
  saldoInicial: number;
  estado: boolean;
  movimiento: number;
  saldoDisponible: number;
}

export interface ReporteResponse {
  movimientos: ReporteLinea[];
  totalDebitos?: number;
  totalCreditos?: number;
  pdfBase64?: string;
}

export interface ReporteParams {
  fechaInicio: string;
  fechaFin: string;
  cliente: number;
}
