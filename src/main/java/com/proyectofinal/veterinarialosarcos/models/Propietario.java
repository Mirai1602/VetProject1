package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "propietario")
public class Propietario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreOwner;

    private String direccion;

    public Propietario(Long id, String nombreOwner, String direccion, String telefono, List<Paciente> pacientes) {
        this.id = id;
        this.nombreOwner = nombreOwner;
        this.direccion = direccion;
        this.telefono = telefono;
        this.pacientes = pacientes;
    }

    public List<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(List<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreOwner() {
        return nombreOwner;
    }

    public void setNombreOwner(String nombreOwner) {
        this.nombreOwner = nombreOwner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String telefono;
    //Un Propietario puede tener varios paciente (mascotas)
    @OneToMany(mappedBy = "owner")
    private List<Paciente> pacientes = new ArrayList<>();


    public Propietario() {

    }
}
