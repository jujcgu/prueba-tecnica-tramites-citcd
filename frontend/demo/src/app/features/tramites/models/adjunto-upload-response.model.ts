export type AdjuntoUploadResponse = {
  identificadorAlmacenamiento: string;
  nombreArchivoOriginal: string;
  tamanoBytes: number;
  tipoMime: string;
  sha256: string;
  urlDescarga: string;
};
