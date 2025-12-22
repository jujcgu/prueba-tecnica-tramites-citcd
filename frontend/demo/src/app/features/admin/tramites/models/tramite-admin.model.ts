export interface AdjuntoDetalle {
  tipoDocumentoNombre: string;
  nombreArchivo: string;
  urlDescarga: string;
}

export interface TramiteDetalle {
  numeroRadicado: number;
  correoSolicitante: string;
  TipoTramiteNombre: string;
  estado: string;
  creadoEn: string;
  ultimoMovimiento: string;
  funcionarioAsignado: string;
  adjuntos: AdjuntoDetalle[];
}
