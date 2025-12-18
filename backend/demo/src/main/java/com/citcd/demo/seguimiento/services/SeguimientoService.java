package com.citcd.demo.seguimiento.services;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.seguimiento.model.Seguimiento;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.seguimiento.repositories.SeguimientoRepository;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SeguimientoService {

    private final SeguimientoRepository seguimientoRepository;

    public void registrar(Tramite tramite,
            Usuario actor,
            TipoEvento tipoEvento,
            EstadoTramite ultimoEstado,
            EstadoTramite nuevoEstado) {

        Seguimiento seg = new Seguimiento();
        seg.setTramiteId(tramite);
        seg.setCreadoPor(actor);
        seg.setTipoEvento(tipoEvento);
        seg.setUltimoEstado(ultimoEstado);
        seg.setNuevoEstado(nuevoEstado);
        seg.setCreadoEn(LocalDate.now(ZoneOffset.UTC));
        seguimientoRepository.save(seg);
    }
}
