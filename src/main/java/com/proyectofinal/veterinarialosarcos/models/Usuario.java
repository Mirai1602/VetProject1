package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "negocio", nullable = false)
    private String nombreNegocio;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "contrasenia", nullable = false)
    private String password;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    public Usuario() {
    }

    public Usuario(String nombreNegocio, String email, String password, LocalDate fechaCreacion) {
        this.nombreNegocio = nombreNegocio;
        this.email = email;
        this.password = password;
        this.fechaCreacion = fechaCreacion;
    }

    public int getId() {
        return id;
    }

    public String getNombreNegocio() {
        return nombreNegocio;
    }

    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    @OneToMany(mappedBy = "usuario")
    private List<Paciente> pacientes;

}