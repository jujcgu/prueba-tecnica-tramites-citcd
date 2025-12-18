package com.citcd.demo.tramite.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record RadicarAdjuntoRequest(
                @NotNull Long tipoDocumentoId,
                @NotBlank String nombreArchivo,
                @NotBlank String storageKey,
                String mimeType,
                @PositiveOrZero Long tamanoBytes,
                @Pattern(regexp = "^[A-Fa-f0-9]{64}$", message = "sha256 debe ser hex de 64 chars") String sha256) {
}
