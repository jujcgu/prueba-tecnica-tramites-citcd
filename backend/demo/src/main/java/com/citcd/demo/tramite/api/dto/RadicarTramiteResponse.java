package com.citcd.demo.tramite.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record RadicarTramiteResponse(
                Long id,
                Long numeroRadicado,
                EstadoTramite estado,
                Long tipoTramiteId,
                String comentario,
                OffsetDateTime creadoEn,
                List<AdjuntoResponse> adjuntos) {
        public record AdjuntoResponse(
                        Long id,
                        Long tipoDocumentoId,
                        String nombreArchivo,
                        String identificadorAlmacenamiento,
                        String tipoMime,
                        long tamanoBytes,
                        String sha256) {
        }
}
