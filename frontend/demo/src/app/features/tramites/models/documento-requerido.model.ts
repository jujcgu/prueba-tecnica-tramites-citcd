export type DocumentoRequerido = {
  tipoDocumentoId: number;
  codigo: string;
  nombre: string;
  descripcion: string;
  esActivo: boolean;
  esObligatorio: boolean;
  orden: number;
  cantidadMinima: number;
  cantidadMaxima: number;
  mimePermitidos: string[];
  tamanoMaxMb: number | null;
};
