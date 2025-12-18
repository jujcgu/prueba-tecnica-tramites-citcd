import { TipoDocumento } from "./tipo-documento";

export type RequisitoDocumental = {
  documento: TipoDocumento;
  obligatorio: boolean;
  orden: number;
};
