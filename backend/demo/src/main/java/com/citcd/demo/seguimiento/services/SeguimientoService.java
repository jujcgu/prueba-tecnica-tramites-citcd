package com.citcd.demo.seguimiento.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.seguimiento.dtos.SeguimientoResponseDTO;
import com.citcd.demo.seguimiento.model.Seguimiento;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.seguimiento.repositories.SeguimientoRepository;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeguimientoService {

	private final SeguimientoRepository seguimientoRepository;

	@Transactional(readOnly = true)
	public List<SeguimientoResponseDTO> listarPorTramiteId(Long numeroRadicado) {
		if (numeroRadicado == null)
			throw new IllegalArgumentException("numeroRadicado es requerido");

		var rows = seguimientoRepository.findByTramiteNumeroRadicado(numeroRadicado);

		return rows.stream().map(p -> new SeguimientoResponseDTO(p.getCreadoEn(), p.getCreadoPorEmail(),
				p.getTipoEvento(), p.getUltimoEstado(), p.getNuevoEstado())).toList();
	}

	@Transactional
	public void registrarCambioEstado(Tramite tramite, Usuario actor, EstadoTramite nuevo) {
		Seguimiento s = base(tramite, actor, TipoEvento.CAMBIO_ESTADO);
		s.setNuevoEstado(nuevo);
		seguimientoRepository.save(s);
	}

	@Transactional
	public void registrarComentario(Tramite tramite, Usuario actor, String comentario) {
		String c = comentario == null ? "" : comentario.trim();
		if (c.isBlank())
			throw new IllegalArgumentException("El comentario no puede ser vacío");

		Seguimiento s = base(tramite, actor, TipoEvento.COMENTARIO);
		s.setComentario(c);
		seguimientoRepository.save(s);
	}

	private Seguimiento base(Tramite tramite, Usuario actor, TipoEvento tipoEvento) {
		Seguimiento s = new Seguimiento();
		s.setTramite(tramite);
		s.setCreadoPor(actor);
		s.setTipoEvento(tipoEvento);
		return s;
	}

}
