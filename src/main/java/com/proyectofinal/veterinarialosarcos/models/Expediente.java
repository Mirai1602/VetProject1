package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.*;

/**
 * Expediente clínico de un paciente (uno por paciente). Guarda los textos de
 * vacunación, desparasitación, control de parásitos y observaciones, que antes
 * vivían directamente en {@link Paciente}.
 */
@Entity
@Table(name = "expediente")
public class Expediente {
    //Id de identificicacion para el expediente
    //No se usa UUID por preferiencia, para que este no sea tan largo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @Column(name = "historial_vacunas", columnDefinition = "text")
    private String historialVacunas;

    @Column(name = "historial_desparasitacion", columnDefinition = "text")
    private String historialDesparasitacion;

    @Column(name = "control_parasitos", columnDefinition = "text")
    private String controlParasitos;

    @Column(name = "observaciones", columnDefinition = "text")
    private String observaciones;

    public Expediente() {
    }

    public Expediente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getHistorialVacunas() {
        return historialVacunas;
    }

    public void setHistorialVacunas(String historialVacunas) {
        this.historialVacunas = historialVacunas;
    }

    public String getHistorialDesparasitacion() {
        return historialDesparasitacion;
    }

    public void setHistorialDesparasitacion(String historialDesparasitacion) {
        this.historialDesparasitacion = historialDesparasitacion;
    }

    public String getControlParasitos() {
        return controlParasitos;
    }

    public void setControlParasitos(String controlParasitos) {
        this.controlParasitos = controlParasitos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
