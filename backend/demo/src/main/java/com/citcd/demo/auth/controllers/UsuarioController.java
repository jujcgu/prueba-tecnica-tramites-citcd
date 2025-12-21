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

    @GetMapping("/funcionarios/activos/combo")
    public ResponseEntity<List<UsuarioComboDTO>> listarFuncionariosActivosParaCombo() {
	return ResponseEntity.ok(service.listarFuncionariosActivosParaCombo());
    }

}
