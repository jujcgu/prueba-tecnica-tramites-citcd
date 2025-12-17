package com.citcd.demo.tramite.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	public Tramite createTramite(TramiteRequestDTO dto) {

		Long numeroRadicado = jdbcTemplate.queryForObject("select nextval('numero_radicado_seq')", Long.class);

		TipoTramite tipoTramite = tipoTramiteRepository.findById(dto.tipoTramiteId()).orElseThrow(
				() -> new EntityNotFoundException("Tipo de tramite no encontrado con id " + dto.tipoTramiteId()));

		Usuario usuario = usuarioAutenticado();

		List<TipoTramiteDocumento> obligatorios = tipoTramiteDocumentoRepository
				.findByTipoTramiteId_IdAndEsObligatorioTrueOrderByOrdenAsc(tipoTramite.getId());

		List<AdjuntoRequestDTO> adjuntos = (dto.adjuntos() == null) ? List.of() : dto.adjuntos();

		validarAdjuntos(tipoTramite, obligatorios, adjuntos);

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

		Usuario funcionario = usuarioRepository
				.findByIdAndRolAndEsActivoTrue(dto.funcionarioId(), RolUsuario.ROLE_ADMINISTRATIVO)
				.orElseThrow(
						() -> new EntityNotFoundException("Funcionario no encontrado con id " + dto.funcionarioId()));

		Tramite tramite = tramiteRepository.findById(requestedId)
				.orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

		if (tramite.getEstado() == EstadoTramite.FINALIZADO || tramite.getEstado() == EstadoTramite.RECHAZADO) {
			throw new IllegalArgumentException(
					"No se puede asignar funcionario: el trámite está FINALIZADO/RECHAZADO.");
		}

		Usuario creadoPor = usuarioAutenticado();

		Seguimiento newSeguimientoRequest = new Seguimiento();
		newSeguimientoRequest.setTramiteId(tramite);
		newSeguimientoRequest.setCreadoPor(creadoPor);
		newSeguimientoRequest.setTipoEvento(TipoEvento.ASIGNACION);
		newSeguimientoRequest.setUltimoEstado(tramite.getEstado());

		tramite.setAsignadoA(funcionario);
		tramite.setActualizadoEn(LocalDate.now());
		Tramite updatedTramite = tramiteRepository.save(tramite);

		newSeguimientoRequest.setNuevoEstado(updatedTramite.getEstado());
		newSeguimientoRequest.setCreadoEn(LocalDate.now());
		seguimientoRepository.save(newSeguimientoRequest);

	}

	@Transactional
	public void actualizarEstadoTramite(Long requestedId, ActualizarEstadoTramiteDTO dto) {

		Tramite tramite = tramiteRepository.findById(requestedId)
				.orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

		Usuario creadoPor = usuarioAutenticado();

		validarTransicion(tramite.getEstado(), dto.estadoTramite());

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

		Tramite tramite = tramiteRepository.findById(requestedId)
				.orElseThrow(() -> new EntityNotFoundException("Tramite no encontrado con id " + requestedId));

		if (tramite.getEstado() == EstadoTramite.FINALIZADO || tramite.getEstado() == EstadoTramite.RECHAZADO) {
			throw new IllegalArgumentException(
					"No se puede agregar el comentario: el trámite está FINALIZADO/RECHAZADO.");
		}

		Usuario creadoPor = usuarioAutenticado();

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

	private void validarAdjuntos(TipoTramite tipoTramite, List<TipoTramiteDocumento> obligatorios,
			List<AdjuntoRequestDTO> adjuntos) {

		var asociados = tipoTramiteDocumentoRepository.findByTipoTramiteId_IdOrderByOrdenAsc(tipoTramite.getId());

		Set<Long> permitidosIds = asociados.stream()
				.map(x -> x.getTipoDocumentoId().getId())
				.collect(Collectors.toSet());

		for (AdjuntoRequestDTO a : adjuntos) {
			if (!permitidosIds.contains(a.tipoDocumentoId())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"El tipoDocumentoId=%d no está asociado al tipoTramiteId=%d. Permitidos=%s"
								.formatted(a.tipoDocumentoId(), tipoTramite.getId(), permitidosIds));
			}
		}

		Set<Long> idsEnviados = adjuntos.stream()
				.map(AdjuntoRequestDTO::tipoDocumentoId)
				.collect(Collectors.toSet());

		var faltantes = obligatorios.stream()
				.map(ttd -> ttd.getTipoDocumentoId().getId())
				.filter(reqId -> !idsEnviados.contains(reqId))
				.toList();

		if (!faltantes.isEmpty()) {
			throw new IllegalArgumentException("Faltan documentos obligatorios: " + faltantes);
		}

		if (idsEnviados.size() != adjuntos.size()) {
			throw new IllegalArgumentException("Hay documentos repetidos (tipoDocumentoId duplicado).");
		}
	}

}
