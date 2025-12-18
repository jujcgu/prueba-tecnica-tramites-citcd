package com.citcd.demo.auth.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import com.citcd.demo.auth.model.enums.RolUsuario;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "usuario", schema = "public", uniqueConstraints = @UniqueConstraint(name = "usuario_email_key", columnNames = "email"), indexes = @Index(name = "ix_usuario_rol_activo", columnList = "rol, es_activo"), check = @CheckConstraint(name = "rol_nombre_chk", constraint = "rol::text = ANY (ARRAY['ROLE_CIUDADANO'::character varying,'ROLE_FUNCIONARIO'::character varying,'ROLE_ADMINISTRATIVO'::character varying]::text[])"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "email", nullable = false, columnDefinition = "citext")
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private RolUsuario rol;

    @Builder.Default
    @Column(name = "es_activo", nullable = false)
    private boolean esActivo = true;

    @Generated(event = { EventType.INSERT })
    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime creadoEn;

    @Generated(event = { EventType.INSERT, EventType.UPDATE })
    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime actualizadoEn;

    @Column(name = "last_login_at", columnDefinition = "timestamp with time zone")
    private OffsetDateTime lastLoginAt;

    public boolean esActivo() {
        return this.esActivo;
    }
}
