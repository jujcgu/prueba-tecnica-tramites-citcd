package com.citcd.demo.auth.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.auth.model.enums.RolUsuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByIdAndRol(Long id, RolUsuario rol);

}
