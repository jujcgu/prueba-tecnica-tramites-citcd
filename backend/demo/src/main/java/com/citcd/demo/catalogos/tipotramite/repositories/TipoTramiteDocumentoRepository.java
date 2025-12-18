package com.citcd.demo.catalogos.tipotramite.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumento;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumentoId;

public interface TipoTramiteDocumentoRepository extends JpaRepository<TipoTramiteDocumento, TipoTramiteDocumentoId> {

	boolean existsByTipoTramiteId_IdAndTipoDocumentoId_Id(Long tipoTramiteId, Long tipoDocumentoId);

	List<TipoTramiteDocumento> findByTipoTramiteId_IdAndEsObligatorioTrueOrderByOrdenAsc(Long tipoTramiteId);

	List<TipoTramiteDocumento> findByTipoTramiteId_IdOrderByOrdenAsc(Long tipoTramiteId);

	@Query("""
			    select ttd
			    from TipoTramiteDocumento ttd
			    join fetch ttd.tipoDocumentoId td
			    where ttd.tipoTramiteId.id = :tipoTramiteId
			      and td.esActivo = true
			    order by ttd.orden asc, td.id asc
			""")
	List<TipoTramiteDocumento> findRequisitosActivos(@Param("tipoTramiteId") long tipoTramiteId);

}
