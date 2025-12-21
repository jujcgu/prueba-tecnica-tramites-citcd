package com.citcd.demo.catalogos.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.catalogos.api.dto.TipoDocumentoDto;
import com.citcd.demo.catalogos.services.ConsultaCatalogosService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoController {

	private final ConsultaCatalogosService service;

	@GetMapping
	public List<TipoDocumentoDto> listar(@RequestParam(name = "activos", required = false) Boolean activos) {
		return service.listarTiposDocumento(activos);
	}
}
