package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import jakarta.persistence.*;

//Crea la entiendad objeto de vacuna, con los diferente tipos de vacunas que puedan existir
@Entity
@Table(name = "Vacunas")
public class Vacuna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private Especie especie;

    private Integer edadMinimaMeses;
    //Si la vacuna es anual, como la rabia
    private boolean anual;
    public Vacuna() {
    }

}
