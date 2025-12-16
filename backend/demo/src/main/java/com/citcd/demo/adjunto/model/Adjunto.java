package com.citcd.demo.adjunto.model;

import java.time.LocalDate;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.catalogos.tipodocumento.model.TipoDocumento;
import com.citcd.demo.tramite.models.Tramite;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Adjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "adjunto_tramite_id_fkey"))
    private Tramite tramiteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "adjunto_tipo_documento_id_fkey"))
    private TipoDocumento tipoDocumentoId;

    private String nombreArchivo;

    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "adjunto_subido_por_fkey"))
    private Usuario subidoPor;

    @Column(nullable = false)
    private LocalDate creadoEn;

}
