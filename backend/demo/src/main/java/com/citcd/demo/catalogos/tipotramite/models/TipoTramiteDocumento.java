package com.citcd.demo.catalogos.tipotramite.models;

import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@IdClass(TipoTramiteDocumentoId.class)
@Data
public class TipoTramiteDocumento {

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_tramite_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_tramite_documento_tipo_tramite_id_fkey"))
	private TipoTramite tipoTramiteId;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_documento_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_tramite_documento_tipo_documento_id_fkey"))
	private TipoDocumento tipoDocumentoId;

	private Boolean esObligatorio;

	private Integer orden;

}
