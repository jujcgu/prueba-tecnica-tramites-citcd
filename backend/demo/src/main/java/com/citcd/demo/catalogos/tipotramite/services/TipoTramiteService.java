package com.citcd.demo.catalogos.tipotramite.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.catalogos.tipotramite.dtos.TipoTramiteComboDTO;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TipoTramiteService {

    private final TipoTramiteRepository repository;

    public List<TipoTramiteComboDTO> findAllForCombo() {
        return repository.findByEsActivoTrue()
                .stream()
                .map(p -> new TipoTramiteComboDTO(p.getId(), p.getNombre()))
                .toList();
    }

}
