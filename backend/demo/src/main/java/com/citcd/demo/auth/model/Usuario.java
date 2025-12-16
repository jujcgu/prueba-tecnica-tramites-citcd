package com.citcd.demo.auth.model;

import java.time.LocalDate;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "usuario_email_key", columnNames = {
        "email" }), check = @CheckConstraint(name = "rol_nombre_chk", constraint = "(rol::text = ANY (ARRAY['ROLE_ESTUDIANTE'::character varying, 'ROLE_DOCENTE'::character varying, 'ROLE_ADMINISTRATIVO'::character varying]::text[])"))
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String rol;

    @Column(nullable = false)
    private Boolean esActivo;

    private LocalDate creadoEn;

    private LocalDate actualizadoEn;

    public boolean esActivo() {
        return this.esActivo;
    }

}
