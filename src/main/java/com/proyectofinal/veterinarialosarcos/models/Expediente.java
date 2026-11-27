package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.*;

/**
 * Carpeta clínica del paciente (una por paciente).
 *
 * <p>No guarda datos clínicos: cada visita se registra como una {@link Consulta} nueva,
 * igual que una hoja más en la carpeta de papel. Así ninguna visita sobrescribe a otra.</p>
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
}
