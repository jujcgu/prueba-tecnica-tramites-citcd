package com.citcd.demo.adjunto.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.adjunto.model.Adjunto;

public interface AdjuntoRepository extends JpaRepository<Adjunto, Long> {

    Boolean existsByIdentificadorAlmacenamiento(String identificadorAlmacenamiento);

    List<Adjunto> findByTramite_IdOrderByIdAsc(Long tramiteId);

}
