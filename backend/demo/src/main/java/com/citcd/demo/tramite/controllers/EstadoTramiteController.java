package com.citcd.demo.tramite.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.tramite.models.enums.EstadoTramite;

@RestController
@RequestMapping("/api/estados-tramite")
public class EstadoTramiteController {

	@GetMapping
	public ResponseEntity<List<String>> listarEstados() {
		List<String> estados = Arrays.stream(EstadoTramite.values()).map(Enum::name).toList();

		return ResponseEntity.ok(estados);
	}
}
