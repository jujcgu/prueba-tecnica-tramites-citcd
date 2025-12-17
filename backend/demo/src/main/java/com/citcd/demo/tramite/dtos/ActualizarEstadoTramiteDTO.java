package com.citcd.demo.tramite.dtos;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoTramiteDTO(
        @NotNull EstadoTramite estadoTramite) {
}
