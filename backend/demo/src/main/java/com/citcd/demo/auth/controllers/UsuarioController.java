package com.citcd.demo.auth.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citcd.demo.auth.dtos.UsuarioComboDTO;
import com.citcd.demo.auth.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService service;

	@GetMapping("/administrativos/activos/combo")
	public ResponseEntity<List<UsuarioComboDTO>> listarAdministrativosActivosParaCombo() {
		return ResponseEntity.ok(service.getAdministrativosActivosParaCombo());
	}

}
