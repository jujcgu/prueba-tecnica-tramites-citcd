package com.citcd.demo.seguimiento.repositories.projections;

import java.time.OffsetDateTime;

import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

public interface SeguimientoResponseProjection {

	OffsetDateTime getCreadoEn();

	String getCreadoPorEmail();

	TipoEvento getTipoEvento();

	EstadoTramite getUltimoEstado();

	EstadoTramite getNuevoEstado();

}
