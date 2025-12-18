package com.citcd.demo.catalogos.api.dto;

public record TipoTramiteDto(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        boolean esActivo) {
}
