package com.citcd.demo.tramite.api.dto;

import java.time.OffsetDateTime;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record TramiteAsignadoResponse(Long numeroRadicado, String correoSolicitante, String TipoTramiteNombre,
		EstadoTramite estado, OffsetDateTime creadoEn, OffsetDateTime ultimoMovimiento) {

}