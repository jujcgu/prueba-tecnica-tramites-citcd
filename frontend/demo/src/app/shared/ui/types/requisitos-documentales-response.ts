import { RequisitoDocumental } from "./requisito-documental";
import { TipoTramite } from "./tipo-tramite";

export type RequisitosDocumentalesResponse = {
  tipoTramite: TipoTramite;
  documentos: RequisitoDocumental[];
};
