package com.citcd.demo.tramite.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record TramiteDetalleResponse(
                Long numeroRadicado,
                String correoSolicitante,
                String TipoTramiteNombre,
                EstadoTramite estado,
                OffsetDateTime creadoEn,
                OffsetDateTime ultimoMovimiento,
                String funcionarioAsignado,
                List<AdjuntoDetalle> adjuntos) {
        public record AdjuntoDetalle(
                        String tipoDocumentoNombre,
                        String nombreArchivo,
                        String urlDescarga) {
        }
}
