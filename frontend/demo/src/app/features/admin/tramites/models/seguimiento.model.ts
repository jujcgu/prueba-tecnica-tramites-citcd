export interface Seguimiento {
  creadoEn: string;
  creadoPorEmail: string;
  tipoEvento: string;
  ultimoEstado: string | null;
  nuevoEstado: string;
}
