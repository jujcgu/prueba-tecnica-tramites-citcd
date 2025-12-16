package com.citcd.demo.tramite.models;

import java.time.LocalDate;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

import jakarta.persistence.CheckConstraint;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "tramite_numero_radicado_key", columnNames = {
        "numeroRadicado" }), check = @CheckConstraint(name = "estado_nombre_chk", constraint = "estado::text = ANY (ARRAY['RADICADO'::character varying, 'EN_PROCESO'::character varying, 'FINALIZADO'::character varying, 'RECHAZADO'::character varying]::text[])"))
@Data
public class Tramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "tramite_radicado_por_fkey"))
    private Usuario radicadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "tramite_tipo_tramite_id_fkey"))
    private TipoTramite tipoTramiteId;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoTramite estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "tramite_asignado_a_fkey"))
    private Usuario asignadoA;

    private Long numeroRadicado;

    private LocalDate finalizadoEn;

    @Column(nullable = false)
    private LocalDate creadoEn;

    private LocalDate actualizadoEn;

}
