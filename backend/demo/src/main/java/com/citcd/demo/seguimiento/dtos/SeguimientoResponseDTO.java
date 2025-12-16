package com.citcd.demo.seguimiento.dtos;

import java.time.LocalDate;

import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record SeguimientoResponseDTO(LocalDate creadoEn, String creadoPorEmail, TipoEvento tipoEvento,
		EstadoTramite ultimoEstado, EstadoTramite nuevoEstado) {

}
