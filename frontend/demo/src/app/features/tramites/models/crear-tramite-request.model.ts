export type CrearTramiteRequest = {
  tipoTramiteId: number;
  comentario: string;
  adjuntos: Array<{
    tipoDocumentoId: number;
    nombreArchivo: string;
    identificadorAlmacenamiento: string;
  }>;
};
