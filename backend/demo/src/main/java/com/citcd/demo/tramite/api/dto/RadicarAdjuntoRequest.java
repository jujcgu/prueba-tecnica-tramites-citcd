package com.citcd.demo.tramite.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RadicarAdjuntoRequest(
        @NotNull Long tipoDocumentoId,
        @NotBlank String nombreArchivo,
        @NotBlank String storageKey,
        String mimeType,
        @PositiveOrZero Long tamanoBytes,
        String sha256) {
}
