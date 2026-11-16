package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

public class AplicacionVacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private Vacuna vacuna;

    private LocalDate fechaAplicacion;

    private LocalDate proximoRefuerzo;
}
