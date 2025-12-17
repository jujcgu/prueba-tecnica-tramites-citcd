package com.citcd.demo.adjunto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdjuntoRequestDTO(
                @NotNull Long tipoDocumentoId,
                @NotBlank @Size(max = 255) String nombreArchivo,
                @NotBlank @Size(max = 2048) String url) {
}
