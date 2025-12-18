package com.citcd.demo.catalogos.tipodocumento.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {

    List<TipoDocumento> findAllByOrderByNombreAsc();

    List<TipoDocumento> findByEsActivoTrueOrderByNombreAsc();
}
