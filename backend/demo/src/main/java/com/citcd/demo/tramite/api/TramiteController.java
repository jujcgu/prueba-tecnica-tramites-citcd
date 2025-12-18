package com.citcd.demo.tramite.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.citcd.demo.seguimiento.services.SeguimientoService;
import com.citcd.demo.tramite.api.dto.AgregarComentario;
import com.citcd.demo.tramite.api.dto.AsignarTramiteRequest;
import com.citcd.demo.tramite.api.dto.CambiarEstadoRequest;
import com.citcd.demo.tramite.api.dto.RadicarTramiteRequest;
import com.citcd.demo.tramite.api.dto.RadicarTramiteResponse;
import com.citcd.demo.tramite.api.dto.TramiteDetalleResponse;
import com.citcd.demo.tramite.services.RadicacionTramiteService;
import com.citcd.demo.tramite.services.TramiteQueryService;
import com.citcd.demo.tramite.services.TramiteWorkflowService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramiteController {

    private final RadicacionTramiteService radicacionTramiteService;
    private final SeguimientoService seguimientoService;
    private final TramiteQueryService tramiteQueryService;
    private final TramiteWorkflowService tramiteWorkflowService;

    @PostMapping
    public ResponseEntity<RadicarTramiteResponse> radicar(@Valid @RequestBody RadicarTramiteRequest request) {
        RadicarTramiteResponse resp = radicacionTramiteService.radicar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resp.id())
                .toUri();

        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/{id}/seguimiento")
    public List<com.citcd.demo.seguimiento.dtos.SeguimientoResponseDTO> seguimiento(@PathVariable("id") Long id) {
        return seguimientoService.listarPorTramiteId(id);
    }

    @GetMapping("/{id}")
    public TramiteDetalleResponse detalle(@PathVariable("id") Long id) {
        return tramiteQueryService.detalle(id);
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<Void> asignar(@PathVariable("id") Long id, @Valid @RequestBody AsignarTramiteRequest req) {
        tramiteWorkflowService.asignar(id, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstado(@PathVariable("id") Long id,
            @Valid @RequestBody CambiarEstadoRequest req) {
        tramiteWorkflowService.cambiarEstado(id, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/comentario")
    public ResponseEntity<Void> agregarComentario(@PathVariable("id") Long requestedId,
            @Valid @RequestBody AgregarComentario dto) {
        tramiteWorkflowService.agregarComentarioTramite(requestedId, dto);
        return ResponseEntity.noContent().build();
    }

}
