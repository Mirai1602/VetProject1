package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.EstadoRevision;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Paciente paciente;

    private LocalDate fecha;

    private String motivoConsulta;

    private String dieta;

    private String comentarios;

    private Double temperatura;

    private Double peso;

    private EstadoRevision aspectoGeneral;

    private EstadoRevision pielMucosas;

    private EstadoRevision respiratorio;

    private EstadoRevision sistemaUrinario;

    private EstadoRevision circulatorio;

    private EstadoRevision digestivo;

    private EstadoRevision ojos;

    private EstadoRevision oidos;

    private EstadoRevision sistemaNervioso;

    private EstadoRevision gangliosLinfaticos;

}
