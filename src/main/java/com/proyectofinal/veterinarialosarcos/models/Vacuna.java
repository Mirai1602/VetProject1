package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import jakarta.persistence.*;

/**
 * Catálogo de vacunas disponibles por especie (Rabia, Parvovirus, Triple felina...).
 * La vacuna que se le pone a un paciente en una visita se registra en {@link VacunaAplicada}.
 */
@Entity
@Table(name = "vacuna")
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false)
    private Especie especie;

    public Vacuna() {
    }

    public Vacuna(String nombre, Especie especie) {
        this.nombre = nombre;
        this.especie = especie;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
