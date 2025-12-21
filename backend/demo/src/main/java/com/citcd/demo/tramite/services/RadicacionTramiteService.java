package com.citcd.demo.tramite.services;

import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.adjunto.model.Adjunto;
import com.citcd.demo.adjunto.model.enums.EstadoAnalisisVirus;
import com.citcd.demo.adjunto.model.enums.ServicioAlmacenamiento;
import com.citcd.demo.adjunto.repositories.AdjuntoRepository;
import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.auth.repositories.UsuarioRepository;
import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;
import com.citcd.demo.catalogos.tipodocumento.repositories.TipoDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramiteDocumento;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteDocumentoRepository;
import com.citcd.demo.catalogos.tipotramite.repositories.TipoTramiteRepository;
import com.citcd.demo.seguimiento.model.Seguimiento;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.seguimiento.repositories.SeguimientoRepository;
import com.citcd.demo.storage.StorageService;
import com.citcd.demo.tramite.api.dto.MetadatosArchivo;
import com.citcd.demo.tramite.api.dto.RadicarAdjuntoRequest;
import com.citcd.demo.tramite.api.dto.RadicarTramiteRequest;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;
import com.citcd.demo.tramite.repositories.TramiteRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RadicacionTramiteService {

	private final TramiteRepository tramiteRepository;
	private final TipoTramiteRepository tipoTramiteRepository;
	private final TipoDocumentoRepository tipoDocumentoRepository;
	private final TipoTramiteDocumentoRepository tipoTramiteDocumentoRepository;
	private final AdjuntoRepository adjuntoRepository;
	private final StorageService storageService;
	private final EntityManager entityManager;
	private final UsuarioRepository usuarioRepository;
	private final SeguimientoRepository seguimientoRepository;

	private Usuario usuarioAutenticado() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = auth.getName();
		return usuarioRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email " + email));
	}

	public Tramite radicar(RadicarTramiteRequest req) {

		Usuario actor = usuarioAutenticado();

		TipoTramite tipoTramite = tipoTramiteRepository.findById(req.tipoTramiteId())
				.orElseThrow(() -> new IllegalArgumentException("TipoTramite no existe: " + req.tipoTramiteId()));

		if (!tipoTramite.esActivo()) {
			throw new IllegalArgumentException("El TipoTramite está inactivo: " + tipoTramite.getId());
		}

		String comentario = (req.comentario() == null) ? "" : req.comentario().trim();
		if (comentario.isBlank())
			throw new IllegalArgumentException("El comentario/descripción no puede estar vacío");

		List<RadicarAdjuntoRequest> adjuntosReq = req.adjuntos() == null ? List.of() : req.adjuntos();

		List<TipoTramiteDocumento> reglas = tipoTramiteDocumentoRepository
				.findRequeridosByTipoTramiteId(tipoTramite.getId());

		Map<Long, TipoTramiteDocumento> reglaPorTipoDoc = reglas.stream()
				.collect(Collectors.toMap(r -> r.getTipoDocumento().getId(), r -> r, (a, b) -> a, LinkedHashMap::new));

		Map<String, MetadatosArchivo> metadatosPorIdentificadorAlmacenamiento = cargarMetadatosArchivos(adjuntosReq);

		validarAdjuntos(adjuntosReq, reglaPorTipoDoc, metadatosPorIdentificadorAlmacenamiento);

		Tramite tramite = Tramite.builder().radicadoPor(actor).tipoTramite(tipoTramite).comentario(comentario)
				.estado(EstadoTramite.RADICADO).build();

		Tramite savedTramite = tramiteRepository.save(tramite);
		tramiteRepository.flush();
		entityManager.refresh(tramite);

		List<Adjunto> adjuntos = new ArrayList<>();

		for (RadicarAdjuntoRequest a : adjuntosReq) {

			TipoDocumento td = tipoDocumentoRepository.findById(a.tipoDocumentoId())
					.orElseThrow(() -> new IllegalArgumentException("TipoDocumento no existe: " + a.tipoDocumentoId()));

			String identificadorAlmacenamiento = a.identificadorAlmacenamiento().trim();

			if (adjuntoRepository.existsByIdentificadorAlmacenamiento(identificadorAlmacenamiento)) {
				throw new IllegalArgumentException(
						"Ese identificador de almacenamiento ya fue usado en la BD: " + identificadorAlmacenamiento);
			}

			MetadatosArchivo meta = metadatosPorIdentificadorAlmacenamiento.get(identificadorAlmacenamiento);
			if (meta == null) {
				throw new IllegalArgumentException("No existe metadata para ese identificador de almacenamiento="
						+ identificadorAlmacenamiento + " (¿archivo no subido o clave incorrecta?)");
			}

			Adjunto adj = Adjunto.builder().tramite(tramite).tipoDocumento(td).nombreArchivo(a.nombreArchivo().trim())
					.subidoPor(actor).servicioAlmacenamiento(ServicioAlmacenamiento.LOCAL)
					.identificadorAlmacenamiento(identificadorAlmacenamiento).tipoMime(meta.tipoMime())
					.tamanoBytes(meta.sizeBytes()).sha256(meta.sha256())
					.estadoAnalisisVirus(EstadoAnalisisVirus.PENDIENTE)
					.estaCuarentenado(false).build();

			adjuntos.add(adj);
		}

		adjuntos = adjuntoRepository.saveAll(adjuntos);

		Seguimiento s = new Seguimiento();
		s.setTramite(savedTramite);
		s.setCreadoPor(actor);
		s.setTipoEvento(TipoEvento.CREACION);
		s.setUltimoEstado(null);
		s.setNuevoEstado(EstadoTramite.RADICADO);
		s.setComentario(null);
		s.setAsignadoA(null);
		s.setCreadoEn(OffsetDateTime.now());
		seguimientoRepository.save(s);

		return savedTramite;

	}

	private Map<String, MetadatosArchivo> cargarMetadatosArchivos(List<RadicarAdjuntoRequest> adjuntosReq) {
		Map<String, MetadatosArchivo> mapMetadatosArchivo = new HashMap<>();
		Set<String> seen = new HashSet<>();

		for (RadicarAdjuntoRequest a : adjuntosReq) {
			String key = a.identificadorAlmacenamiento().trim();
			if (!seen.add(key)) {
				throw new IllegalArgumentException("identificadorAlmacenamiento repetido en la solicitud: " + key);
			}

			Path path = storageService.load(key);
			if (path == null || !Files.exists(path)) {
				throw new IllegalArgumentException("No existe el archivo para identificadorAlmacenamiento: " + key);
			}

			try {
				long size = Files.size(path);

				if (a.tamanoBytes() != null && a.tamanoBytes() >= 0 && a.tamanoBytes() != size) {
					throw new IllegalArgumentException(
							"tamanoBytes no coincide para " + key + ". enviado=" + a.tamanoBytes() + " real=" + size);
				}

				String mime = normalizeMime(a.tipoMime(), path, key);

				String sha = (a.sha256() != null && !a.sha256().isBlank()) ? a.sha256().trim().toLowerCase()
						: sha256File(path);

				mapMetadatosArchivo.put(key, new MetadatosArchivo(size, mime, sha));

			} catch (IllegalArgumentException e) {
				throw e;
			} catch (Exception e) {
				throw new IllegalArgumentException(
						"No se pudo leer metadata del archivo " + key + ": " + e.getMessage());
			}
		}
		return mapMetadatosArchivo;
	}

	private static String normalizeMime(String requestMime, Path file, String nameFallback) {
		String mime = requestMime == null ? "" : requestMime.trim();
		if (mime.isBlank() || "application/octet-stream".equalsIgnoreCase(mime)) {
			try {
				String probed = Files.probeContentType(file);
				if (probed != null && !probed.isBlank())
					return probed;
			} catch (Exception ignored) {
			}
			String guessed = URLConnection.guessContentTypeFromName(nameFallback);
			return (guessed == null || guessed.isBlank()) ? "application/octet-stream" : guessed;
		}
		return mime;
	}

	private static String sha256File(Path path) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		try (InputStream is = Files.newInputStream(path); DigestInputStream dis = new DigestInputStream(is, md)) {
			dis.transferTo(java.io.OutputStream.nullOutputStream());
		}
		byte[] hash = md.digest();
		StringBuilder sb = new StringBuilder(hash.length * 2);
		for (byte b : hash)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

	private void validarAdjuntos(List<RadicarAdjuntoRequest> adjuntosReq,
			Map<Long, TipoTramiteDocumento> reglaPorTipoDoc,
			Map<String, MetadatosArchivo> metaPorIdentificadorAlmacenamiento) {

		for (RadicarAdjuntoRequest a : adjuntosReq) {
			if (!reglaPorTipoDoc.containsKey(a.tipoDocumentoId())) {
				throw new IllegalArgumentException(
						"El tipoDocumentoId=" + a.tipoDocumentoId() + " no está configurado para este TipoTramite");
			}
		}

		for (RadicarAdjuntoRequest a : adjuntosReq) {
			TipoTramiteDocumento regla = reglaPorTipoDoc.get(a.tipoDocumentoId());
			MetadatosArchivo meta = metaPorIdentificadorAlmacenamiento.get(a.identificadorAlmacenamiento().trim());

			if (!regla.esMimePermitido(meta.tipoMime())) {
				throw new IllegalArgumentException("Mime no permitido para tipoDocumentoId=" + a.tipoDocumentoId()
						+ ". Mime=" + meta.tipoMime() + ". Permitidos=" + regla.getMimePermitidos());
			}

			Integer maxMb = regla.getTamanoMaxMb();
			if (maxMb != null && maxMb > 0) {
				long maxBytes = (long) maxMb * 1024L * 1024L;
				if (meta.sizeBytes() > maxBytes) {
					throw new IllegalArgumentException("Archivo supera tamaño máximo para tipoDocumentoId="
							+ a.tipoDocumentoId() + ". Max=" + maxMb + "MB, recibido=" + meta.sizeBytes() + " bytes");
				}
			}
		}

		Map<Long, Long> conteo = adjuntosReq.stream()
				.collect(Collectors.groupingBy(RadicarAdjuntoRequest::tipoDocumentoId, Collectors.counting()));

		for (var e : reglaPorTipoDoc.entrySet()) {
			Long tipoDocId = e.getKey();
			TipoTramiteDocumento regla = e.getValue();
			long count = conteo.getOrDefault(tipoDocId, 0L);

			if (count < regla.getCantidadMinima()) {
				throw new IllegalArgumentException("Faltan adjuntos para tipoDocumentoId=" + tipoDocId + ". Min="
						+ regla.getCantidadMinima() + ", enviados=" + count);
			}
			if (regla.getCantidadMaxima() >= 0 && count > regla.getCantidadMaxima()) {
				throw new IllegalArgumentException("Demasiados adjuntos para tipoDocumentoId=" + tipoDocId + ". Max="
						+ regla.getCantidadMaxima() + ", enviados=" + count);
			}
		}
	}
}
