package com.citcd.demo.tramite.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.citcd.demo.adjunto.model.Adjunto;
import com.citcd.demo.adjunto.repositories.AdjuntoRepository;
import com.citcd.demo.tramite.api.dto.TramiteAsignadoResponse;
import com.citcd.demo.tramite.api.dto.TramiteDetalleResponse;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;
import com.citcd.demo.tramite.repositories.TramiteRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TramiteQueryService {

	private final TramiteRepository tramiteRepository;
	private final AdjuntoRepository adjuntoRepository;

	@Transactional(readOnly = true)
	public TramiteDetalleResponse detalle(Long tramiteId) {
		var t = tramiteRepository.findById(tramiteId)
				.orElseThrow(() -> new EntityNotFoundException("Trámite no existe: " + tramiteId));

		var adjuntos = adjuntoRepository.findByTramite_IdOrderByIdAsc(tramiteId);

		String base = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

		List<TramiteDetalleResponse.AdjuntoDetalle> adjDtos = adjuntos.stream()
				.map(a -> new TramiteDetalleResponse.AdjuntoDetalle(
						a.getTipoDocumento().getNombre(), a.getNombreArchivo(),
						base + "/api/adjuntos/files/" + a.getIdentificadorAlmacenamiento()))
				.toList();

		String tipoNombre = (t.getTipoTramite() != null) ? t.getTipoTramite().getNombre() : null;
		String radicadoPor = (t.getRadicadoPor() != null) ? t.getRadicadoPor().getEmail() : null;

		String asignadoA = null;
		try {
			var m = t.getClass().getMethod("getAsignadoA");
			Object u = m.invoke(t);
			if (u != null) {
				var getEmail = u.getClass().getMethod("getEmail");
				asignadoA = (String) getEmail.invoke(u);
			}
		} catch (Exception ignored) {
		}

		return new TramiteDetalleResponse(t.getNumeroRadicado(),
				radicadoPor, t.getTipoTramite().getNombre(), t.getEstado(), t.getCreadoEn(), t.getActualizadoEn(),
				asignadoA, adjDtos);
	}

	@Transactional(readOnly = true)
	public List<TramiteDetalleResponse> listarDetallePorEstado(EstadoTramite estado) {

		List<Tramite> tramites = tramiteRepository.findByEstadoOrderByIdAsc(estado);
		if (tramites.isEmpty())
			return List.of();

		List<Long> ids = tramites.stream().map(Tramite::getId).toList();

		List<Adjunto> adjuntos = adjuntoRepository.findByTramite_IdInOrderByTramite_IdAscIdAsc(ids);

		// Agrupar por tramiteId
		Map<Long, List<Adjunto>> adjuntosPorTramiteId = adjuntos.stream()
				.collect(Collectors.groupingBy(a -> a.getTramite().getId(), LinkedHashMap::new, Collectors.toList()));

		String base = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();

		return tramites.stream()
				.map(t -> {
					List<Adjunto> adj = adjuntosPorTramiteId.getOrDefault(t.getId(), List.of());

					List<TramiteDetalleResponse.AdjuntoDetalle> adjDtos = adj.stream()
							.map(a -> new TramiteDetalleResponse.AdjuntoDetalle(
									a.getTipoDocumento().getNombre(),
									a.getNombreArchivo(),
									base + "/api/adjuntos/files/" + a.getIdentificadorAlmacenamiento()))
							.toList();

					String tipoNombre = (t.getTipoTramite() != null) ? t.getTipoTramite().getNombre() : null;
					String radicadoPor = (t.getRadicadoPor() != null) ? t.getRadicadoPor().getEmail() : null;
					String asignadoA = extraerAsignadoAEmail(t);

					return new TramiteDetalleResponse(
							t.getNumeroRadicado(),
							radicadoPor,
							tipoNombre,
							t.getEstado(),
							t.getCreadoEn(),
							t.getActualizadoEn(),
							asignadoA,
							adjDtos);
				})
				.toList();
	}

	private String extraerAsignadoAEmail(Object tramiteEntity) {
		try {
			var m = tramiteEntity.getClass().getMethod("getAsignadoA");
			Object u = m.invoke(tramiteEntity);
			if (u != null) {
				var getEmail = u.getClass().getMethod("getEmail");
				return (String) getEmail.invoke(u);
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<TramiteAsignadoResponse> getByfuncionarioId(long id) {
		List<TramiteAsignadoResponse> list = tramiteRepository
				.findByaAsignadoAId(id).stream().map(t -> new TramiteAsignadoResponse(t.numeroRadicado(),
						t.correoSolicitante(), t.TipoTramiteNombre(), t.estado(), t.creadoEn(), t.ultimoMovimiento()))
				.toList();
		return list;

	}
}
