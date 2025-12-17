package com.citcd.demo.catalogos.tipotramite.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.catalogos.tipotramite.dtos.TipoTramiteComboDTO;
import com.citcd.demo.catalogos.tipotramite.services.TipoTramiteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-tramite")
@RequiredArgsConstructor
public class TipoTramiteController {

	private final TipoTramiteService service;

	@GetMapping("/activos/combo")
	public ResponseEntity<List<TipoTramiteComboDTO>> listarActivosParaCombo() {
		return ResponseEntity.ok(service.getActivosParaCombo());
	}

}
