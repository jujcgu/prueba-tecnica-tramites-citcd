package com.citcd.demo.catalogos.tipotramite.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumento;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumentoId;

public interface TipoTramiteDocumentoRepository extends JpaRepository<TipoTramiteDocumento, TipoTramiteDocumentoId> {

	@Query("""
			    select ttd
			    from TipoTramiteDocumento ttd
			    join fetch ttd.tipoDocumento td
			    where ttd.tipoTramite.id = :tipoTramiteId
			    order by coalesce(ttd.orden, 999999), td.nombre
			""")
	List<TipoTramiteDocumento> findRequeridosByTipoTramiteId(@Param("tipoTramiteId") Long tipoTramiteId);
}
