package com.citcd.demo.catalogos.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.catalogos.api.dto.DocumentoRequeridoDto;
import com.citcd.demo.catalogos.api.dto.TipoTramiteDto;
import com.citcd.demo.catalogos.services.ConsultaCatalogosService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-tramite")
@RequiredArgsConstructor
public class TipoTramiteController {

	private final ConsultaCatalogosService service;

	@GetMapping
	public List<TipoTramiteDto> listar(@RequestParam(name = "activos", required = false) Boolean activos) {
		return service.listarTiposTramite(activos);
	}

	@GetMapping("/{id}/documentos-requeridos")
	public List<DocumentoRequeridoDto> documentosRequeridos(@PathVariable("id") Long id,
			@RequestParam(name = "obligatorios", required = false) Boolean obligatorios) {
		return service.listarDocumentosRequeridos(id, obligatorios);
	}
}
