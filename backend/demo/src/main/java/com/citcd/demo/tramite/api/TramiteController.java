package com.citcd.demo.tramite.api;

import java.net.URI;
import java.util.Arrays;
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

import com.citcd.demo.seguimiento.dtos.SeguimientoResponseDTO;
import com.citcd.demo.seguimiento.services.SeguimientoService;
import com.citcd.demo.tramite.api.dto.AgregarComentario;
import com.citcd.demo.tramite.api.dto.AsignarTramiteRequest;
import com.citcd.demo.tramite.api.dto.CambiarEstadoRequest;
import com.citcd.demo.tramite.api.dto.RadicarTramiteRequest;
import com.citcd.demo.tramite.api.dto.TramiteAsignadoResponse;
import com.citcd.demo.tramite.api.dto.TramiteDetalleResponse;
import com.citcd.demo.tramite.models.enums.EstadoTramite;
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
    public ResponseEntity<Void> radicar(@Valid @RequestBody RadicarTramiteRequest request) {
        long id = radicacionTramiteService.radicar(request).getId();

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public TramiteDetalleResponse detalle(@PathVariable("id") Long id) {
        return tramiteQueryService.detalle(id);
    }

    @PutMapping("/{numeroRadicado}/asignar")
    public ResponseEntity<Void> asignar(@PathVariable("numeroRadicado") Long id,
            @Valid @RequestBody AsignarTramiteRequest req) {
        tramiteWorkflowService.asignar(id, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/comentario")
    public ResponseEntity<Void> agregarComentario(@PathVariable("id") Long requestedId,
            @Valid @RequestBody AgregarComentario dto) {
        tramiteWorkflowService.agregarComentarioTramite(requestedId, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{numeroRadicado}/estado")
    public ResponseEntity<Void> actualizarEstado(@PathVariable("numeroRadicado") Long id,
            @Valid @RequestBody CambiarEstadoRequest req) {
        tramiteWorkflowService.cambiarEstado(id, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{numeroRadicado}/seguimiento")
    public List<SeguimientoResponseDTO> seguimiento(@PathVariable("numeroRadicado") Long numeroRadicado) {
        return seguimientoService.listarPorTramiteId(numeroRadicado);
    }

    @GetMapping("/funcionario/{id}")
    public List<TramiteAsignadoResponse> listarPorFuncionario(@PathVariable("id") Long id) {
        return tramiteQueryService.getByfuncionarioId(id);
    }

    @GetMapping("/estado/{estado}/detalle")
    public List<TramiteDetalleResponse> listarPorEstadoDetalle(
            @PathVariable("estado") EstadoTramite estado) {
        return tramiteQueryService.listarDetallePorEstado(estado);
    }

    @GetMapping("/estados")
    public List<String> listarEstados() {
        return Arrays.stream(EstadoTramite.values())
                .map(Enum::name)
                .toList();
    }

}
