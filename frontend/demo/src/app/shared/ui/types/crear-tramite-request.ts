import { TramiteAdjuntoRequest } from "./tramite-adjunto-request";

export type CrearTramiteRequest = {
  tipoTramiteId: number;
  comentario: string;
  adjuntos: TramiteAdjuntoRequest[];
};
