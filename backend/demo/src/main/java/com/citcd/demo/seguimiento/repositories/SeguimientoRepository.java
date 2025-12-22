package com.citcd.demo.seguimiento.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citcd.demo.seguimiento.model.Seguimiento;
import com.citcd.demo.seguimiento.repositories.projections.SeguimientoResponseProjection;

public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

	@Query("""
			    select
			        s.creadoEn as creadoEn,
			        s.creadoPor.email as creadoPorEmail,
			        s.tipoEvento as tipoEvento,
			        s.ultimoEstado as ultimoEstado,
			        s.nuevoEstado as nuevoEstado
			    from Seguimiento s
			    where s.tramite.numeroRadicado = :numeroRadicado
			    order by s.creadoEn asc, s.id asc
			""")
	List<SeguimientoResponseProjection> findByTramiteNumeroRadicado(@Param("numeroRadicado") Long numeroRadicado);
}
