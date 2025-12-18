package com.citcd.demo.catalogos.tipotramite.models;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tipo_tramite_documento", schema = "public", indexes = {
		@Index(name = "tipo_tramite_documento_tipo_documento_id_index", columnList = "tipo_documento_id"),
		@Index(name = "tipo_tramite_documento_tipo_tramite_id_index", columnList = "tipo_tramite_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "tipoTramite", "tipoDocumento" })
public class TipoTramiteDocumento {

	@EmbeddedId
	@EqualsAndHashCode.Include
	private TipoTramiteDocumentoId id;

	@MapsId("tipoTramiteId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tipo_tramite_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_tramite_documento_tipo_tramite_id_fkey"))
	private TipoTramite tipoTramite;

	@MapsId("tipoDocumentoId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "tipo_documento_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_tramite_documento_tipo_documento_id_fkey"))
	private TipoDocumento tipoDocumento;

	@Builder.Default
	@Column(name = "es_obligatorio", nullable = false)
	private boolean esObligatorio = false;

	@Column(name = "orden")
	private Integer orden;

	@Builder.Default
	@Column(name = "cantidad_minima", nullable = false)
	private int cantidadMinima = 0;

	@Builder.Default
	@Column(name = "cantidad_maxima", nullable = false)
	private int cantidadMaxima = 1;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "mime_permitidos", columnDefinition = "jsonb")
	@Builder.Default
	private Set<String> mimePermitidos = new LinkedHashSet<>(List.of(
			"application/pdf",
			"image/jpeg",
			"image/png",
			"application/msword",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document"));

	public boolean esMimePermitido(String mime) {
		return mime != null && mimePermitidos != null && mimePermitidos.contains(mime);
	}

	@Column(name = "tamano_max_mb")
	private Integer tamanoMaxMb;
}
