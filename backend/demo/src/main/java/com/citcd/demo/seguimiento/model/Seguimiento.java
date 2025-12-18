package com.citcd.demo.seguimiento.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "seguimiento", schema = "public")
@Data
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tramite_id", nullable = false)
    private Tramite tramite;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creado_por", nullable = false)
    private Usuario creadoPor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "ultimo_estado")
    private EstadoTramite ultimoEstado;

    @Enumerated(EnumType.STRING)
    @Column(name = "nuevo_estado")
    private EstadoTramite nuevoEstado;

    @Column(name = "comentario", columnDefinition = "text")
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a")
    private Usuario asignadoA;

    @Column(name = "creado_en", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime creadoEn;

    @PrePersist
    void prePersist() {
        if (creadoEn == null) {
            creadoEn = OffsetDateTime.now(ZoneOffset.UTC);
        }
    }
}
