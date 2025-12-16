package com.citcd.demo.tramite.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.citcd.demo.adjunto.dtos.AdjuntoRequestDTO;
import com.citcd.demo.adjunto.model.Adjunto;
import com.citcd.demo.adjunto.repositories.AdjuntoRepository;
import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.auth.model.enums.RolUsuario;
import com.citcd.demo.auth.repositories.UsuarioRepository;
import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;
import com.citcd.demo.catalogos.tipodocumento.repositories.TipoDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumento;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteRepository;
import com.citcd.demo.seguimiento.dtos.SeguimientoResponseDTO;
import com.citcd.demo.seguimiento.model.Seguimiento;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.seguimiento.repositories.SeguimientoRepository;
import com.citcd.demo.tramite.dtos.ActualizarEstadoTramiteDTO;
import com.citcd.demo.tramite.dtos.AgregarComentarioTramiteDTO;
import com.citcd.demo.tramite.dtos.AsignarFuncionarioTramiteDTO;
import com.citcd.demo.tramite.dtos.TramiteRequestDTO;
import com.citcd.demo.tramite.dtos.TramiteResponseDTO;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;
import com.citcd.demo.tramite.repositories.TramiteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramiteService {

	private final TramiteRepository tramiteRepository;
	private final AdjuntoRepository adjuntoRepository;
	private final UsuarioRepository usuarioRepository;
	private final TipoTramiteRepository tipoTramiteRepository;
	private final TipoTramiteDocumentoRepository tipoTramiteDocumentoRepository;
	private final TipoDocumentoRepository tipoDocumentoRepository;
	private final SeguimientoRepository seguimientoRepository;
	private final JdbcTemplate jdbcTemplate;

	@Transactional
	public Tramite createTramite(TramiteRequestDTO dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Long numeroRadicado = jdbcTemplate.queryForObject("select nextval('numero_radicado_seq')", Long.class);

		TipoTramite tipoTramite = tipoTramiteRepository.findById(dto.tipoTramiteId()).orElseThrow(
				() -> new EntityNotFoundException("Tipo de tramite no encontrado con id " + dto.tipoTramiteId()));

		Usuario usuario = usuarioRepository.findByEmail(authentication.getName()).orElseThrow(
				() -> new EntityNotFoundException("Usuario no encontrado con email " + authentication.getName()));

		List<TipoTramiteDocumento> obligatorios = tipoTramiteDocumentoRepository
				.findByTipoTramiteId_IdAndEsObligatorioTrueOrderByOrdenAsc(tipoTramite.getId());

		List<AdjuntoRequestDTO> adjuntos = (dto.adjuntos() == null) ? List.of() : dto.adjuntos();

		for (AdjuntoRequestDTO a : adjuntos) {
			boolean permitido = tipoTramiteDocumentoRepository
					.existsByTipoTramiteId_IdAndTipoDocumentoId_Id(tipoTramite.getId(), a.tipoDocumentoId());

			if (!permitido) {
				var permitidos = tipoTramiteDocumentoRepository
						.findByTipoTramiteId_IdOrderByOrdenAsc(tipoTramite.getId()).stream()
						.map(x -> x.getTipoDocumentoId().getId()).toList();

				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"El tipoDocumentoId=%d no está asociado al tipoTramiteId=%d. Permitidos=%s"
								.formatted(a.tipoDocumentoId(), tipoTramite.getId(), permitidos));
			}
		}

		Set<Long> idsEnviados = adjuntos.stream().map(AdjuntoRequestDTO::tipoDocumentoId)
				.collect(java.util.stream.Collectors.toSet());

		List<Long> faltantes = obligatorios.stream().map(ttd -> ttd.getTipoDocumentoId().getId())
				.filter(reqId -> !idsEnviados.contains(reqId)).toList();

		if (!faltantes.isEmpty()) {
			throw new IllegalArgumentException("Faltan documentos obligatorios: " + faltantes);
		}

		Tramite newTramiteRequest = new Tramite();
		newTramiteRequest.setRadicadoPor(usuario);
		newTramiteRequest.setTipoTramiteId(tipoTramite);
		newTramiteRequest.setComentario(dto.comentario());
		newTramiteRequest.setEstado(EstadoTramite.RADICADO);
		newTramiteRequest.setNumeroRadicado(numeroRadicado);
		newTramiteRequest.setCreadoEn(LocalDate.now());

		Tramite savedTramite = tramiteRepository.save(newTramiteRequest);

		List<Adjunto> adjuntosEntity = new java.util.ArrayList<>();
		for (AdjuntoRequestDTO a : adjuntos) {
			TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(a.tipoDocumentoId()).orElseThrow(
					() -> new EntityNotFoundException("Tipo de documento no encontrado con id " + a.tipoDocumentoId()));

			Adjunto newAdjunto = new Adjunto();
			newAdjunto.setTramiteId(savedTramite);
			newAdjunto.setTipoDocumentoId(tipoDocumento);
			newAdjunto.setNombreArchivo(a.nombreArchivo());
			newAdjunto.setUrl(a.url());
			newAdjunto.setSubidoPor(usuario);
			newAdjunto.setCreadoEn(LocalDate.now());
			adjuntosEntity.add(newAdjunto);
		}
		adjuntoRepository.saveAll(adjuntosEntity);

		Seguimiento newSeguimientoRequest = new Seguimiento();
		newSeguimientoRequest.setTramiteId(savedTramite);
		newSeguimientoRequest.setCreadoPor(usuario);
		newSeguimientoRequest.setTipoEvento(TipoEvento.CREACION);
		newSeguimientoRequest.setNuevoEstado(savedTramite.getEstado());
		newSeguimientoRequest.setCreadoEn(LocalDate.now());

		seguimientoRepository.save(newSeguimientoRequest);

		return savedTramite;

	}

	@Transactional
	public void asignarFuncionarioTramite(Long requestedId, AsignarFuncionarioTramiteDTO dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Usuario usuario = usuarioRepository.findByIdAndRol(dto.funcionarioId(), RolUsuario.ROLE_ADMINISTRATIVO)
				.orElseThrow(
						() -> new EntityNotFoundException("Funcionario no encontrado con id " + dto.funcionarioId()));

		Tramite tramite = tramiteRepository.findById(requestedId)
				.orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

		Usuario creadoPor = usuarioRepository.findByEmail(authentication.getName()).orElseThrow(
				() -> new EntityNotFoundException("Usuario no encontrado con email " + authentication.getName()));

		Seguimiento newSeguimientoRequest = new Seguimiento();
		newSeguimientoRequest.setTramiteId(tramite);
		newSeguimientoRequest.setCreadoPor(creadoPor);
		newSeguimientoRequest.setTipoEvento(TipoEvento.ASIGNACION);
		newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

		tramite.setAsignadoA(usuario);
		tramite.setActualizadoEn(LocalDate.now());
		Tramite updatedTramite = tramiteRepository.save(tramite);

		newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
		newSeguimientoRequest.setCreadoEn(LocalDate.now());
		seguimientoRepository.save(newSeguimientoRequest);

	}

	@Transactional
	public void actualizarEstadoTramite(Long requestedId, ActualizarEstadoTramiteDTO dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Tramite tramite = tramiteRepository.findById(requestedId)
				.orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

		Usuario creadoPor = usuarioRepository.findByEmail(authentication.getName()).orElseThrow(
				() -> new EntityNotFoundException("Usuario no encontrado con email " + authentication.getName()));

		if (tramite.getEstado() == EstadoTramite.FINALIZADO || tramite.getEstado() == EstadoTramite.RECHAZADO) {
			throw new IllegalArgumentException("No se puede actualizar el estado del tramite.");
		}

		if (dto.estadoTramite() == EstadoTramite.FINALIZADO) {
			tramite.setFinalizadoEn(LocalDate.now());
		}

		Seguimiento newSeguimientoRequest = new Seguimiento();
		newSeguimientoRequest.setTramiteId(tramite);
		newSeguimientoRequest.setCreadoPor(creadoPor);
		newSeguimientoRequest.setTipoEvento(TipoEvento.CAMBIO_ESTADO);
		newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

		tramite.setEstado(dto.estadoTramite());
		tramite.setActualizadoEn(LocalDate.now());
		Tramite updatedTramite = tramiteRepository.save(tramite);

		newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
		newSeguimientoRequest.setCreadoEn(LocalDate.now());
		seguimientoRepository.save(newSeguimientoRequest);

	}

	@Transactional
	public void agregarComentarioTramite(Long requestedId, AgregarComentarioTramiteDTO dto) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Tramite tramite = tramiteRepository.findById(requestedId)
				.orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

		Usuario creadoPor = usuarioRepository.findByEmail(authentication.getName()).orElseThrow(
				() -> new EntityNotFoundException("Usuario no encontrado con email " + authentication.getName()));

		Seguimiento newSeguimientoRequest = new Seguimiento();
		newSeguimientoRequest.setTramiteId(tramite);
		newSeguimientoRequest.setCreadoPor(creadoPor);
		newSeguimientoRequest.setTipoEvento(TipoEvento.COMENTARIO);
		newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

		tramite.setComentario(dto.comentario());
		tramite.setActualizadoEn(LocalDate.now());
		Tramite updatedTramite = tramiteRepository.save(tramite);

		newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
		newSeguimientoRequest.setCreadoEn(LocalDate.now());
		seguimientoRepository.save(newSeguimientoRequest);

	}

	public List<TramiteResponseDTO> findTramiteByFuncionarioId(Long funcionarioId) {

		var list = tramiteRepository.findByAsignadoAId(funcionarioId);

		return list.stream()
				.map(p -> new TramiteResponseDTO(p.getId(), p.getRadicadoPorId(), p.getRadicadoPorEmail(),
						p.getTipoTramiteNombre(), p.getComentario(), p.getEstado(), p.getNumeroRadicado(),
						p.getFinalizadoEn()))
				.toList();
	}

	public List<SeguimientoResponseDTO> findSeguimientoByTramiteId(Long tramiteId) {
		var list = seguimientoRepository.findByTramiteId(tramiteId);

		return list.stream().map(p -> new SeguimientoResponseDTO(p.getCreadoEn(), p.getCreadoPorEmail(),
				p.getTipoEvento(), p.getUltimoEstado(), p.getNuevoEstado())).toList();
	}

}
