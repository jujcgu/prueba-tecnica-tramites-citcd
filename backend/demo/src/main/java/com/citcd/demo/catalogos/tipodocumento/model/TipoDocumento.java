package com.citcd.demo.catalogos.tipodocumento.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tipo_documento", schema = "public", uniqueConstraints = @UniqueConstraint(name = "tipo_documento_codigo_key", columnNames = "codigo"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-z0-9_.]+$", message = "El código solo permite minúsculas, números, _ y .")
    @Column(name = "codigo", nullable = false)
    private String codigo;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @Column(name = "es_activo", nullable = false)
    private boolean esActivo;

    @Column(name = "creado_en", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime actualizadoEn;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        if (creadoEn == null)
            creadoEn = now;
        if (actualizadoEn == null)
            actualizadoEn = now;
    }

    @PreUpdate
    void preUpdate() {
        actualizadoEn = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public boolean esActivo() {
        return this.esActivo;
    }
}
