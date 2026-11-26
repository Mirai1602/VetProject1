package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import jakarta.persistence.*;

import java.time.LocalDate;

//Crea la entiendad objeto de vacuna, con los diferente tipos de vacunas que puedan existir
@Entity
@Table(name = "Vacunas")
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Consulta consulta;

    @ManyToOne
    private Vacuna vacuna;

    private LocalDate fechaAplicacion;
    public Vacuna() {
    }

}
