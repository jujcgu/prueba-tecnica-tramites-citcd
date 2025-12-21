package com.citcd.demo.catalogos.services;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.catalogos.api.dto.DocumentoRequeridoDto;
import com.citcd.demo.catalogos.api.dto.TipoDocumentoDto;
import com.citcd.demo.catalogos.api.dto.TipoTramiteDto;
import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;
import com.citcd.demo.catalogos.tipodocumento.repositories.TipoDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumento;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaCatalogosService {

	private final TipoTramiteRepository tipoTramiteRepository;
	private final TipoDocumentoRepository tipoDocumentoRepository;
	private final TipoTramiteDocumentoRepository tipoTramiteDocumentoRepository;

	@Transactional(readOnly = true)
	public List<TipoTramiteDto> listarTiposTramite(Boolean activos) {
		List<TipoTramite> items = Boolean.TRUE.equals(activos)
				? tipoTramiteRepository.findByEsActivoTrueOrderByNombreAsc()
				: tipoTramiteRepository.findAllByOrderByNombreAsc();

		return items.stream().map(this::toDto).toList();
	}

	@Transactional(readOnly = true)
	public List<TipoDocumentoDto> listarTiposDocumento(Boolean activos) {
		List<TipoDocumento> items = Boolean.TRUE.equals(activos)
				? tipoDocumentoRepository.findByEsActivoTrueOrderByNombreAsc()
				: tipoDocumentoRepository.findAllByOrderByNombreAsc();

		return items.stream().map(this::toDto).toList();
	}

	@Transactional(readOnly = true)
	public List<DocumentoRequeridoDto> listarDocumentosRequeridos(Long tipoTramiteId, Boolean obligatorios) {
		if (!tipoTramiteRepository.existsById(tipoTramiteId)) {
			throw new EntityNotFoundException("TipoTramite no existe: " + tipoTramiteId);
		}

		List<TipoTramiteDocumento> reglas = tipoTramiteDocumentoRepository.findRequeridosByTipoTramiteId(tipoTramiteId);

		return reglas.stream().filter(r -> !Boolean.TRUE.equals(obligatorios) || r.isEsObligatorio()).map(this::toDto)
				.toList();
	}

	private TipoTramiteDto toDto(TipoTramite e) {
		return new TipoTramiteDto(e.getId(), e.getCodigo(), e.getNombre(), e.getDescripcion(), e.isEsActivo());
	}

	private TipoDocumentoDto toDto(TipoDocumento e) {
		return new TipoDocumentoDto(e.getId(), e.getCodigo(), e.getNombre(), e.getDescripcion(), e.isEsActivo());
	}

	private DocumentoRequeridoDto toDto(TipoTramiteDocumento r) {
		TipoDocumento td = r.getTipoDocumento();

		Set<String> mimes = (r.getMimePermitidos() == null) ? Collections.emptySet() : r.getMimePermitidos();

		return new DocumentoRequeridoDto(td.getId(), td.getCodigo(), td.getNombre(), td.getDescripcion(),
				td.isEsActivo(), r.isEsObligatorio(), r.getOrden(), r.getCantidadMinima(), r.getCantidadMaxima(), mimes,
				r.getTamanoMaxMb());
	}
}
