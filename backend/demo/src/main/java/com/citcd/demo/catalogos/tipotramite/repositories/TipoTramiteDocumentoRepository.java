package com.citcd.demo.catalogos.tipotramite.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumento;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumentoId;

public interface TipoTramiteDocumentoRepository extends JpaRepository<TipoTramiteDocumento, TipoTramiteDocumentoId> {

	boolean existsByTipoTramiteId_IdAndTipoDocumentoId_Id(Long tipoTramiteId, Long tipoDocumentoId);

	List<TipoTramiteDocumento> findByTipoTramiteId_IdAndEsObligatorioTrueOrderByOrdenAsc(Long tipoTramiteId);

	List<TipoTramiteDocumento> findByTipoTramiteId_IdOrderByOrdenAsc(Long tipoTramiteId);
}
