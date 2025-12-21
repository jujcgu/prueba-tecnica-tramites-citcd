package com.citcd.demo.tramite.models;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.citcd.demo.auth.model.Usuario;
import com.citcd.demo.catalogos.tipotramite.models.TipoTramite;
import com.citcd.demo.tramite.models.enums.EstadoTramite;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tramite", schema = "public", uniqueConstraints = @UniqueConstraint(name = "tramite_numero_radicado_key", columnNames = "numero_radicado"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "radicadoPor", "tipoTramite", "asignadoA" })
public class Tramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "radicado_por", nullable = false, foreignKey = @ForeignKey(name = "tramite_radicado_por_fkey"))
    private Usuario radicadoPor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_tramite_id", nullable = false, foreignKey = @ForeignKey(name = "tramite_tipo_tramite_id_fkey"))
    private TipoTramite tipoTramite;

    @Column(name = "comentario", nullable = false, columnDefinition = "text")
    private String comentario;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoTramite estado = EstadoTramite.RADICADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_a", foreignKey = @ForeignKey(name = "tramite_asignado_a_fkey"))
    private Usuario asignadoA;

    @Generated(event = { EventType.INSERT })
    @Column(name = "numero_radicado", nullable = false, updatable = false, insertable = false)
    private Long numeroRadicado;

    @Column(name = "finalizado_en", columnDefinition = "timestamp with time zone")
    private OffsetDateTime finalizadoEn;

    @Generated(event = { EventType.INSERT })
    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime creadoEn;

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime actualizadoEn;
}
