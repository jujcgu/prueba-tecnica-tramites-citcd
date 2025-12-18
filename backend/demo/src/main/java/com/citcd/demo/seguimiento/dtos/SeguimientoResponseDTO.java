package com.citcd.demo.seguimiento.dtos;

import java.time.OffsetDateTime;

import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record SeguimientoResponseDTO(OffsetDateTime creadoEn, String creadoPorEmail, TipoEvento tipoEvento,
		EstadoTramite ultimoEstado, EstadoTramite nuevoEstado) {

}
