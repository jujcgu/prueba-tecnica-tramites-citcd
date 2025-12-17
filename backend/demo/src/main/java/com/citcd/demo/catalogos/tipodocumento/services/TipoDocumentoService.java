package com.citcd.demo.catalogos.tipodocumento.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.catalogos.tipodocumento.dtos.TipoDocumentoComboDTO;
import com.citcd.demo.catalogos.tipodocumento.repositories.TipoDocumentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoDocumentoService {

	private final TipoDocumentoRepository repository;

	public List<TipoDocumentoComboDTO> getActivosParaCombo() {
		return repository.findByEsActivoTrue().stream().map(p -> new TipoDocumentoComboDTO(p.getId(), p.getNombre()))
				.toList();
	}

}
