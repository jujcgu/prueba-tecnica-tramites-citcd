package com.citcd.demo.tramite.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citcd.demo.tramite.api.dto.TramiteAsignadoResponse;
import com.citcd.demo.tramite.models.Tramite;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    @Query(value = "select t.numeroRadicado, t.radicadoPor.email, t.tipoTramite.nombre, t.estado, t.creadoEn, t.actualizadoEn "
	    + "from Tramite t where t.asignadoA.id = :id")
    List<TramiteAsignadoResponse> findByaAsignadoAId(@Param("id") Long id);

}
