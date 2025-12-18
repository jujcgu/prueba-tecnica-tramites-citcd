package com.citcd.demo.catalogos.tipotramite.dtos;

import java.util.List;

import com.citcd.demo.catalogos.tipodocumento.dtos.DocumentoRequisitoDTO;

public record RequisitosDocumentalesResponse(TipoTramiteDTO tipoTramite, List<DocumentoRequisitoDTO> documentos) {

}
