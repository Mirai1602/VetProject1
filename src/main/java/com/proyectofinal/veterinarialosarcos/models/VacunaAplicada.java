package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.*;

import java.time.LocalDate;

/** Una vacuna aplicada durante una consulta (renglón "Vacuna + Fecha" de la hoja física). */
@Entity
@Table(name = "vacuna_aplicada")
public class VacunaAplicada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vacuna_id", nullable = false)
    private Vacuna vacuna;

    @Column(name = "fecha_aplicacion", nullable = false)
    private LocalDate fechaAplicacion;

    public VacunaAplicada() {
    }

    public VacunaAplicada(Vacuna vacuna, LocalDate fechaAplicacion) {
        this.vacuna = vacuna;
        this.fechaAplicacion = fechaAplicacion;
    }

    public Long getId() {
        return id;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public Vacuna getVacuna() {
        return vacuna;
    }

    public void setVacuna(Vacuna vacuna) {
        this.vacuna = vacuna;
    }

    public LocalDate getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(LocalDate fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }
}
