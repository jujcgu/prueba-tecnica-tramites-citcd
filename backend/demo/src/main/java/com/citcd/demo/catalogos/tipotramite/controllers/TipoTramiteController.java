package com.citcd.demo.catalogos.tipotramite.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.catalogos.tipotramite.dtos.RequisitosDocumentalesResponse;
import com.citcd.demo.catalogos.tipotramite.dtos.TipoTramiteComboDTO;
import com.citcd.demo.catalogos.tipotramite.services.RequisitosDocumentalesService;
import com.citcd.demo.catalogos.tipotramite.services.TipoTramiteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-tramite")
@RequiredArgsConstructor
public class TipoTramiteController {

	private final TipoTramiteService tipoTramiteService;
	private final RequisitosDocumentalesService requisitosDocumentalesService;

	@GetMapping("/activos/combo")
	public ResponseEntity<List<TipoTramiteComboDTO>> listarActivosParaCombo() {
		return ResponseEntity.ok(tipoTramiteService.getActivosParaCombo());
	}

	@GetMapping("/{id}/requisitos-documentales")
	public RequisitosDocumentalesResponse requisitos(
			@org.springframework.web.bind.annotation.PathVariable("id") long id) {
		return requisitosDocumentalesService.obtener(id);
	}

}
