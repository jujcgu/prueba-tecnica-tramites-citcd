package com.citcd.demo.tramite.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RadicarTramiteRequest(@NotNull Long tipoTramiteId,
        @Size(max = 280) String comentario,
        List<@Valid RadicarAdjuntoRequest> adjuntos) {

}
