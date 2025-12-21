package com.citcd.demo.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.auth.dtos.UsuarioComboDTO;
import com.citcd.demo.auth.model.enums.RolUsuario;
import com.citcd.demo.auth.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public List<UsuarioComboDTO> listarFuncionariosActivosParaCombo() {
	return repository.findByRolAndEsActivoTrue(RolUsuario.ROLE_FUNCIONARIO).stream()
		.map(p -> new UsuarioComboDTO(p.getId(), p.getEmail())).toList();
    }

}
