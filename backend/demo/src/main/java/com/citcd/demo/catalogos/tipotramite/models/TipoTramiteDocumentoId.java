package com.citcd.demo.catalogos.tipotramite.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoTramiteDocumentoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "tipo_tramite_id", nullable = false)
    private Long tipoTramiteId;

    @Column(name = "tipo_documento_id", nullable = false)
    private Long tipoDocumentoId;
}
