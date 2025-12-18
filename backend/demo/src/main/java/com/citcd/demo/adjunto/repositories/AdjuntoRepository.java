package com.citcd.demo.adjunto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.adjunto.model.Adjunto;

public interface AdjuntoRepository extends JpaRepository<Adjunto, Long> {

    Boolean existsByStorageKey(String storageKey);

}
