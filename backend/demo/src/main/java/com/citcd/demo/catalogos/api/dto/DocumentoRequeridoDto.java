package com.citcd.demo.catalogos.api.dto;

import java.util.Set;

public record DocumentoRequeridoDto(Long tipoDocumentoId, String codigo, String nombre, String descripcion,
		boolean esActivo, boolean esObligatorio, Integer orden, int cantidadMinima, int cantidadMaxima,
		Set<String> mimePermitidos, Integer tamanoMaxMb) {
}
