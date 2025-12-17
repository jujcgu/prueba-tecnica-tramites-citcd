package com.citcd.demo.catalogos.tipodocumento.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.catalogos.tipodocumento.dtos.TipoDocumentoComboDTO;
import com.citcd.demo.catalogos.tipodocumento.services.TipoDocumentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoController {

	private final TipoDocumentoService service;

	@GetMapping("/activos/combo")
	public ResponseEntity<List<TipoDocumentoComboDTO>> listarActivosParaCombo() {
		return ResponseEntity.ok(service.getActivosParaCombo());
	}

}
