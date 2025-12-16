package com.citcd.demo.tramite.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.repositories.projections.TramiteResponseProjection;

public interface TramiteRepository extends JpaRepository<Tramite, Long> {

    @Query("""
                select
                    t.id as id,
                    t.radicadoPor.id as radicadoPorId,
                    t.radicadoPor.email as radicadoPorEmail,
                    t.tipoTramiteId.nombre as tipoTramiteNombre,
                    t.comentario as comentario,
                    t.estado as estado,
                    t.numeroRadicado as numeroRadicado,
                    t.finalizadoEn as finalizadoEn
                from Tramite t
                where t.asignadoA.id = :id
            """)
    List<TramiteResponseProjection> findByAsignadoAId(@Param("id") Long id);

}
