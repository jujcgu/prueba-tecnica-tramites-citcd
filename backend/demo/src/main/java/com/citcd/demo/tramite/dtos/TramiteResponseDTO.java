package com.citcd.demo.tramite.dtos;

import java.time.LocalDate;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record TramiteResponseDTO(Long id, Long radicadoPorId, String radicadoPorEmail, String tipoTramiteNombre,
		String comentario, EstadoTramite estado, Long numeroRadicado, LocalDate finalizadoEn) {
}
