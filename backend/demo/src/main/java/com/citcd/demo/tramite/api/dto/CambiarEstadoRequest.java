package com.citcd.demo.tramite.api.dto;

import jakarta.validation.constraints.NotNull;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record CambiarEstadoRequest(
                @NotNull EstadoTramite nuevoEstado) {
}
