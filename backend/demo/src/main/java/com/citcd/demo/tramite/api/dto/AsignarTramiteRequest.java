package com.citcd.demo.tramite.api.dto;

import jakarta.validation.constraints.NotNull;

public record AsignarTramiteRequest(
        @NotNull Long funcionarioId) {
}
