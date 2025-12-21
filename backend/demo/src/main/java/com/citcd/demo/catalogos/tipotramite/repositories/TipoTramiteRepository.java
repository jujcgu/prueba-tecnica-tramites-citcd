package com.citcd.demo.catalogos.tipotramite.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;

public interface TipoTramiteRepository extends JpaRepository<TipoTramite, Long> {

    List<TipoTramite> findAllByOrderByNombreAsc();

    List<TipoTramite> findByEsActivoTrueOrderByIdAsc();
}
