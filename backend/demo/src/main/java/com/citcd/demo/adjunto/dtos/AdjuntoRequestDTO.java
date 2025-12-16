package com.citcd.demo.adjunto.dtos;

public record AdjuntoRequestDTO(
        Long tipoDocumentoId,
        String nombreArchivo,
        String url) {

}
