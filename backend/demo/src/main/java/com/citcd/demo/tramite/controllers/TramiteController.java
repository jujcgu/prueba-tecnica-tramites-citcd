package com.citcd.demo.tramite.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.citcd.demo.tramite.dtos.ActualizarEstadoTramiteDTO;
import com.citcd.demo.tramite.dtos.AsignarFuncionarioTramiteDTO;
import com.citcd.demo.tramite.dtos.TramiteRequestDTO;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.services.TramiteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramiteController {

    private final TramiteService service;

    @PostMapping
    public ResponseEntity<Void> createTramite(@Valid @RequestBody TramiteRequestDTO newTramiteRequest,
            UriComponentsBuilder ucb) {
        Tramite savedTramite = service.createTramite(newTramiteRequest);
        URI locationOfNewCashCard = ucb
                .path("/api/tramites/{id}")
                .buildAndExpand(savedTramite.getId())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<Void> asignarFuncionarioTramite(
            @PathVariable("id") Long requestedId,
            @Valid @RequestBody AsignarFuncionarioTramiteDTO dto) {
        service.asignarFuncionarioTramite(requestedId, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<Void> actualizarEstadoTramite(
            @PathVariable("id") Long requestedId,
            @Valid @RequestBody ActualizarEstadoTramiteDTO dto) {
        service.actualizarEstadoTramite(requestedId, dto);
        return ResponseEntity.noContent().build();
    }

}
