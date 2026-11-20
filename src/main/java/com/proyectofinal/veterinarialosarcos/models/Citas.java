package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.EstadoCaso;
import jakarta.persistence.*;

import java.time.LocalDateTime;

//Cada cita pertenece a un paciente; se usa para saber cuándo fue su última visita
@Entity
@Table(name = "citas")
public class Citas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    private String motivo;

    //Sirve para contar los casos resueltos / resolviendo / por resolver de cada veterinario
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCaso estado = EstadoCaso.POR_RESOLVER;

    public Citas() {
    }

    public Citas(Paciente paciente, LocalDateTime fechaHora, String motivo) {
        this.paciente = paciente;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCaso getEstado() {
        return estado;
    }

    public void setEstado(EstadoCaso estado) {
        this.estado = estado;
    }
}
