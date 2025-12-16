package com.citcd.demo.seguimiento.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.seguimiento.model.Seguimiento;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

}
