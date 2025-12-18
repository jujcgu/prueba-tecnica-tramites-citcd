package com.citcd.demo.catalogos.tipotramite.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.catalogos.tipodocumento.dtos.DocumentoRequisitoDTO;
import com.citcd.demo.catalogos.tipodocumento.dtos.TipoDocumentoDTO;
import com.citcd.demo.catalogos.tipotramite.dtos.RequisitosDocumentalesResponse;
import com.citcd.demo.catalogos.tipotramite.dtos.TipoTramiteDTO;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequisitosDocumentalesService {

	private final TipoTramiteRepository tipoTramiteRepository;
	private final TipoTramiteDocumentoRepository tipoTramiteDocumentoRepository;

	public RequisitosDocumentalesResponse obtener(long tipoTramiteId) {

		var tt = tipoTramiteRepository.findById(tipoTramiteId).filter(TipoTramite::esActivo)
				.orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
						org.springframework.http.HttpStatus.NOT_FOUND, "Tipo de trámite no existe o está inactivo"));

		var requisitos = tipoTramiteDocumentoRepository.findRequisitosActivos(tipoTramiteId);

		var documentos = requisitos.stream()
				.map(ttd -> new DocumentoRequisitoDTO(new TipoDocumentoDTO(ttd.getTipoDocumentoId().getId(),
						ttd.getTipoDocumentoId().getCodigo(), ttd.getTipoDocumentoId().getNombre(),
						ttd.getTipoDocumentoId().getDescripcion(), ttd.getTipoDocumentoId().esActivo()),
						ttd.getEsObligatorio(), ttd.getOrden()))
				.toList();

		return new RequisitosDocumentalesResponse(
				new TipoTramiteDTO(tt.getId(), tt.getCodigo(), tt.getNombre(), tt.getDescripcion(), tt.esActivo()),
				documentos);

	}

}
