package com.citcd.demo.seguimiento.model;

import java.time.LocalDate;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.seguimiento.model.enums.TipoEvento;
import com.citcd.demo.tramite.models.Tramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

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
import lombok.Data;

@Entity
@Data
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tramite_id", nullable = false, foreignKey = @ForeignKey(name = "seguimiento_tramite_id_fkey"))
    private Tramite tramiteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por", nullable = false, foreignKey = @ForeignKey(name = "seguimiento_creado_por_fkey"))
    private Usuario creadoPor;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    private EstadoTramite ultimoEstado;

    @Enumerated(EnumType.STRING)
    private EstadoTramite nuevoEstado;

    private LocalDate creadoEn;

}
