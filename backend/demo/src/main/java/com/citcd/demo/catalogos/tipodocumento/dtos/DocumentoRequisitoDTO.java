package com.citcd.demo.catalogos.tipodocumento.dtos;

public record DocumentoRequisitoDTO(TipoDocumentoDTO documento, boolean obligatorio, int orden) {

}
