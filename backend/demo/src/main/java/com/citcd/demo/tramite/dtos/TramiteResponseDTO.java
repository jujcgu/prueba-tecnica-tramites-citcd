package com.citcd.demo.tramite.dtos;

import java.time.LocalDate;

public record TramiteResponseDTO(
        Long id,
        String radicadoPor,
        Long tipoTramiteId,
        String descripcion,
        String estado,
        String asignadoA,
        Long numeroRadicado,
        LocalDate finalizadoEn) {

}
