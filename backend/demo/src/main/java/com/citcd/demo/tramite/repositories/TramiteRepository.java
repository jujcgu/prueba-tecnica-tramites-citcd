package com.citcd.demo.tramite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.tramite.models.Tramite;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {

}
