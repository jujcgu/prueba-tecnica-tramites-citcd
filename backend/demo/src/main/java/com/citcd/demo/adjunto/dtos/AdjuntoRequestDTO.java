package com.citcd.demo.adjunto.dtos;

public record AdjuntoRequestDTO(
        Long tramiteId,
        Long tipoDocumentoId,
        String nombreArchivo,
        String url) {

}
