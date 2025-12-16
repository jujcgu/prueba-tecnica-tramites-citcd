package com.citcd.demo.seguimiento.repositories.projections;

import java.time.LocalDate;

import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

public interface SeguimientoResponseProjection {

	LocalDate getCreadoEn();

	String getCreadoPorEmail();

	TipoEvento getTipoEvento();

	EstadoTramite getUltimoEstado();

	EstadoTramite getNuevoEstado();

}
