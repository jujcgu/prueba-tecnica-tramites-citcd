package com.citcd.demo.adjunto.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.citcd.demo.adjunto.model.enums.EstadoAnalisisVirus;
import com.citcd.demo.adjunto.model.enums.ServicioAlmacenamiento;
import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;
import com.citcd.demo.tramite.models.Tramite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "tramite", "tipoDocumento", "subidoPor" })
public class Adjunto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	@EqualsAndHashCode.Include
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tramite_id", nullable = false, foreignKey = @ForeignKey(name = "adjunto_tramite_id_fkey"))
	private Tramite tramite;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tipo_documento_id", nullable = false, foreignKey = @ForeignKey(name = "adjunto_tipo_documento_id_fkey"))
	private TipoDocumento tipoDocumento;

	@Column(name = "nombre_archivo", nullable = false)
	private String nombreArchivo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "subido_por", nullable = false, foreignKey = @ForeignKey(name = "adjunto_subido_por_fkey"))
	private Usuario subidoPor;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "servicio_almacenamiento", nullable = false)
	private ServicioAlmacenamiento servicioAlmacenamiento = ServicioAlmacenamiento.LOCAL;

	@Column(name = "identificador_almacenamiento", nullable = false)
	private String identificadorAlmacenamiento;

	@Column(name = "tipo_mime", nullable = false)
	private String tipoMime;

	@Column(name = "tamano_bytes", nullable = false)
	private long tamanoBytes;

	@Column(name = "sha256", nullable = false)
	private String sha256;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "estado_analisis_virus", nullable = false)
	private EstadoAnalisisVirus estadoAnalisisVirus = EstadoAnalisisVirus.PENDIENTE;

	@Generated(event = { EventType.INSERT, EventType.UPDATE })
	@Column(name = "analizado_en", columnDefinition = "timestamp with time zone")
	private OffsetDateTime analizadoEn;

	@Builder.Default
	@Column(name = "esta_cuarentenado", nullable = false)
	private boolean estaCuarentenado = false;

	@Generated(event = { EventType.INSERT })
	@Column(name = "creado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
	private OffsetDateTime creadoEn;

	public void marcarComoClean() {
		this.estadoAnalisisVirus = EstadoAnalisisVirus.SEGURO;
		this.estaCuarentenado = false;
	}

	public void marcarComoInfected() {
		this.estadoAnalisisVirus = EstadoAnalisisVirus.AMENAZA;
		this.estaCuarentenado = true;
	}
}
