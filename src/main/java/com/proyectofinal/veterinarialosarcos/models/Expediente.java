package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

public class Expediente {
    //Id de identificicacion para el expediente
    //No se usa UUID por preferiencia, para que este no sea tan largo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Paciente paciente;


}
