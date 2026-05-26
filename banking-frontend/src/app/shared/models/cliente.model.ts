export interface Cliente {
  clienteId?: string;
  nombre: string;
  genero: string;
  edad: number;
  identificacion: string;
  direccion: string;
  telefono: string;
  contrasena?: string;
  estado: boolean;
}
