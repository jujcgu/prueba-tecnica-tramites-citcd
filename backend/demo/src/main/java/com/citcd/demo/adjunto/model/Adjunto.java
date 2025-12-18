package com.citcd.demo.adjunto.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.citcd.demo.adjunto.model.enums.StorageBackend;
import com.citcd.demo.adjunto.model.enums.VirusScanStatus;
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
import jakarta.persistence.Index;
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
@Table(name = "adjunto", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "ux_adj_tram_arch_ver", columnNames = { "tramite_id", "nombre_archivo", "version" }),
        @UniqueConstraint(name = "ux_adjunto_path", columnNames = { "storage_key" })
}, indexes = {
        @Index(name = "ix_adj_subido_por", columnList = "subido_por"),
        @Index(name = "ix_adj_tram_tipodoc", columnList = "tramite_id, tipo_documento_id"),
        @Index(name = "ix_adj_tramite_fecha", columnList = "tramite_id, creado_en"),
        @Index(name = "ix_adjunto_sha256", columnList = "sha256")
})
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
    @Column(name = "storage_backend", nullable = false)
    private StorageBackend storageBackend = StorageBackend.FS;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "tamano_bytes", nullable = false)
    private long tamanoBytes;

    @Column(name = "sha256", nullable = false)
    private String sha256;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private int version = 1;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "virus_scan_status", nullable = false)
    private VirusScanStatus virusScanStatus = VirusScanStatus.PENDING;

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "virus_scan_at", columnDefinition = "timestamp with time zone")
    private OffsetDateTime virusScanAt;

    @Builder.Default
    @Column(name = "quarantine", nullable = false)
    private boolean quarantine = false;

    @Generated(event = { EventType.INSERT })
    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime creadoEn;

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime actualizadoEn;

    public void marcarComoClean() {
        this.virusScanStatus = VirusScanStatus.CLEAN;
        this.quarantine = false;
    }

    public void marcarComoInfected() {
        this.virusScanStatus = VirusScanStatus.INFECTED;
        this.quarantine = true;
    }
}
