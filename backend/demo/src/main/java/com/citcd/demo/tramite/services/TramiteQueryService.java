package com.citcd.demo.tramite.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.citcd.demo.adjunto.repositories.AdjuntoRepository;
import com.citcd.demo.tramite.api.dto.TramiteDetalleResponse;
import com.citcd.demo.tramite.repositories.TramiteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramiteQueryService {

    private final TramiteRepository tramiteRepository;
    private final AdjuntoRepository adjuntoRepository;

    @Transactional(readOnly = true)
    public TramiteDetalleResponse detalle(Long tramiteId) {
        var t = tramiteRepository.findById(tramiteId)
                .orElseThrow(() -> new EntityNotFoundException("Trámite no existe: " + tramiteId));

        var adjuntos = adjuntoRepository.findByTramite_IdOrderByIdAsc(tramiteId);

        String base = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

        List<TramiteDetalleResponse.AdjuntoDetalle> adjDtos = adjuntos.stream()
                .map(a -> new TramiteDetalleResponse.AdjuntoDetalle(
                        a.getId(),
                        a.getTipoDocumento().getId(),
                        a.getNombreArchivo(),
                        a.getStorageKey(),
                        a.getMimeType(),
                        a.getTamanoBytes(),
                        a.getSha256(),
                        base + "/api/adjuntos/files/" + a.getStorageKey()))
                .toList();

        String tipoNombre = (t.getTipoTramite() != null) ? t.getTipoTramite().getNombre() : null;
        String radicadoPor = (t.getRadicadoPor() != null) ? t.getRadicadoPor().getEmail() : null;

        String asignadoA = null;
        try {
            var m = t.getClass().getMethod("getAsignadoA");
            Object u = m.invoke(t);
            if (u != null) {
                var getEmail = u.getClass().getMethod("getEmail");
                asignadoA = (String) getEmail.invoke(u);
            }
        } catch (Exception ignored) {
        }

        return new TramiteDetalleResponse(
                t.getId(),
                t.getNumeroRadicado(),
                t.getEstado(),
                t.getTipoTramite().getId(),
                tipoNombre,
                t.getComentario(),
                t.getCreadoEn(),
                radicadoPor,
                asignadoA,
                adjDtos);
    }
}
