package com.citcd.demo.tramite.dtos;

import java.util.List;

import com.citcd.demo.adjunto.dtos.AdjuntoRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TramiteRequestDTO(
        @NotNull Long tipoTramiteId,
        @Size(max = 280) String comentario,
        List<@Valid AdjuntoRequestDTO> adjuntos) {
}
