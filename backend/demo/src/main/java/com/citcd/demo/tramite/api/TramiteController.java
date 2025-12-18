package com.citcd.demo.tramite.api;

import java.net.URI;

import com.citcd.demo.tramite.api.dto.RadicarTramiteRequest;
import com.citcd.demo.tramite.api.dto.RadicarTramiteResponse;
import com.citcd.demo.tramite.services.RadicacionTramiteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramiteController {

    private final RadicacionTramiteService radicacionTramiteService;

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
}
