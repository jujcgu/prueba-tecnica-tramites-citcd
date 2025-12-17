package com.citcd.demo.auth.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.auth.model.enums.RolUsuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	Optional<Usuario> findByIdAndRolAndEsActivoTrue(Long id, RolUsuario rol);

	List<Usuario> findByRolAndEsActivoTrue(RolUsuario rol);

}
