package com.citcd.demo.catalogos.tipotramite.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "tipo_tramite_codigo_key", columnNames = {
        "codigo" }))
@Data
public class TipoTramite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Column(nullable = false)
    private Boolean esActivo;

    @Column(nullable = false)
    private LocalDate creadoEn;

    private LocalDate actualizadoEn;

    public boolean esActivo() {
        return this.esActivo;
    }

}
