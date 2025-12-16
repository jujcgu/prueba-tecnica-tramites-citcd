package com.citcd.demo.tramite.repositories.projections;

import java.time.LocalDate;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public interface TramiteResponseProjection {
    Long getId();

    Long getRadicadoPorId();

    String getRadicadoPorEmail();

    String getTipoTramiteNombre();

    String getComentario();

    EstadoTramite getEstado();

    Long getNumeroRadicado();

    LocalDate getFinalizadoEn();
}