package com.citcd.demo.tramite.services;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.auth.model.enums.RolUsuario;
import com.citcd.demo.auth.repositories.UsuarioRepository;
import com.citcd.demo.seguimiento.model.Seguimiento;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.seguimiento.repositories.SeguimientoRepository;
import com.citcd.demo.tramite.api.dto.AgregarComentario;
import com.citcd.demo.tramite.api.dto.AsignarTramiteRequest;
import com.citcd.demo.tramite.api.dto.CambiarEstadoRequest;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;
import com.citcd.demo.tramite.repositories.TramiteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramiteWorkflowService {

    private final TramiteRepository tramiteRepository;
    private final UsuarioRepository usuarioRepository;
    private final SeguimientoRepository seguimientoRepository;

    private static final Map<EstadoTramite, Set<EstadoTramite>> TRANSICIONES = Map.of(
            EstadoTramite.RADICADO, Set.of(EstadoTramite.EN_PROCESO, EstadoTramite.RECHAZADO),
            EstadoTramite.EN_PROCESO, Set.of(EstadoTramite.FINALIZADO, EstadoTramite.RECHAZADO));

    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email " + email));
    }

    @Transactional
    public void asignar(Long numeroRadicado, AsignarTramiteRequest dto) {

        Usuario funcionario = usuarioRepository
                .findByIdAndRolAndEsActivoTrue(dto.funcionarioId(), RolUsuario.ROLE_FUNCIONARIO)
                .orElseThrow(
                        () -> new IllegalArgumentException("Funcionario no encontrado con id " + dto.funcionarioId()));

        Tramite tramite = tramiteRepository.findByNumeroRadicado(numeroRadicado)
                .orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + numeroRadicado));

        if (tramite.getEstado() == EstadoTramite.FINALIZADO || tramite.getEstado() == EstadoTramite.RECHAZADO) {
            throw new IllegalArgumentException(
                    "No se puede asignar funcionario: el trámite está FINALIZADO/RECHAZADO.");
        }

        Usuario creadoPor = usuarioAutenticado();

        Seguimiento newSeguimientoRequest = new Seguimiento();
        newSeguimientoRequest.setTramite(tramite);
        newSeguimientoRequest.setCreadoPor(creadoPor);
        newSeguimientoRequest.setTipoEvento(TipoEvento.ASIGNACION);
        newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

        tramite.setAsignadoA(funcionario);
        tramite.setEstado(EstadoTramite.EN_PROCESO);
        tramite.setActualizadoEn(OffsetDateTime.now());
        Tramite updatedTramite = tramiteRepository.save(tramite);

        newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
        newSeguimientoRequest.setCreadoEn(OffsetDateTime.now());
        seguimientoRepository.save(newSeguimientoRequest);

    }

    @Transactional
    public void cambiarEstado(Long numeroRadicado, CambiarEstadoRequest dto) {
        Tramite tramite = tramiteRepository.findByNumeroRadicado(numeroRadicado)
                .orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + numeroRadicado));

        Usuario creadoPor = usuarioAutenticado();

        validarTransicion(tramite.getEstado(), dto.nuevoEstado());

        if (dto.nuevoEstado() == EstadoTramite.FINALIZADO) {
            tramite.setFinalizadoEn(OffsetDateTime.now());
        }

        Seguimiento newSeguimientoRequest = new Seguimiento();
        newSeguimientoRequest.setTramite(tramite);
        newSeguimientoRequest.setCreadoPor(creadoPor);
        newSeguimientoRequest.setTipoEvento(TipoEvento.CAMBIO_ESTADO);
        newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

        tramite.setEstado(dto.nuevoEstado());
        tramite.setActualizadoEn(OffsetDateTime.now());
        Tramite updatedTramite = tramiteRepository.save(tramite);

        newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
        newSeguimientoRequest.setCreadoEn(OffsetDateTime.now());
        seguimientoRepository.save(newSeguimientoRequest);
    }

    @Transactional
    public void agregarComentarioTramite(Long requestedId, AgregarComentario dto) {
        Tramite tramite = tramiteRepository.findById(requestedId)
                .orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

        if (tramite.getEstado() == EstadoTramite.FINALIZADO || tramite.getEstado() == EstadoTramite.RECHAZADO) {
            throw new IllegalArgumentException(
                    "No se puede agregar el comentario: el trámite está FINALIZADO/RECHAZADO.");
        }

        Usuario creadoPor = usuarioAutenticado();

        Seguimiento newSeguimientoRequest = new Seguimiento();
        newSeguimientoRequest.setComentario(dto.comentario());
        newSeguimientoRequest.setTramite(tramite);
        newSeguimientoRequest.setCreadoPor(creadoPor);
        newSeguimientoRequest.setTipoEvento(TipoEvento.COMENTARIO);
        newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

        tramite.setComentario(dto.comentario());
        tramite.setActualizadoEn(OffsetDateTime.now());
        Tramite updatedTramite = tramiteRepository.save(tramite);

        newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
        newSeguimientoRequest.setCreadoEn(OffsetDateTime.now());
        seguimientoRepository.save(newSeguimientoRequest);

    }

    private void validarTransicion(EstadoTramite actual, EstadoTramite nuevo) {

        if (nuevo == null)
            throw new IllegalArgumentException("estadoTramite es obligatorio");

        if (actual == nuevo)
            throw new IllegalArgumentException("El trámite ya está en el estado " + actual);

        var permitidos = TRANSICIONES.getOrDefault(actual, Set.of());

        if (!permitidos.contains(nuevo)) {
            throw new IllegalArgumentException(
                    "Transición no permitida: " + actual + " -> " + nuevo + ". Permitidas=" + permitidos);
        }

    }

}
