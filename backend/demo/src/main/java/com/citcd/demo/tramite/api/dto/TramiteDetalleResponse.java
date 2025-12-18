package com.citcd.demo.tramite.api.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

public record TramiteDetalleResponse(
        Long id,
        Long numeroRadicado,
        EstadoTramite estado,
        Long tipoTramiteId,
        String tipoTramiteNombre,
        String comentario,
        OffsetDateTime creadoEn,
        String radicadoPorEmail,
        String asignadoAEmail,
        List<AdjuntoDetalle> adjuntos) {
    public record AdjuntoDetalle(
            Long id,
            Long tipoDocumentoId,
            String nombreArchivo,
            String storageKey,
            String mimeType,
            long tamanoBytes,
            String sha256,
            String downloadUrl) {
    }
}
