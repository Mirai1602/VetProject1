package com.proyectofinal.veterinarialosarcos.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Nombre de usuario con el que se inicia sesión
    @Column(name = "negocio", nullable = false)
    private String nombreNegocio;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    //Se guarda cifrada (ver utils.Contrasenas)
    @Column(name = "contrasenia", nullable = false)
    private String password;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    //Datos personales del perfil (opcionales)
    @Column(name = "nombres")
    private String nombre;

    @Column(name = "apellidos")
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    //Foto de perfil guardada como bytes (bytea en PostgreSQL)
    @Column(name = "foto")
    private byte[] foto;

    @OneToMany(mappedBy = "usuario")
    private List<Paciente> pacientes;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    /** Edad en años según la fecha de nacimiento, o null si no se ha capturado. */
    public Integer getEdad() {
        return fechaNacimiento == null ? null : Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /** Nombre y apellido si existen; si no, el nombre de usuario. */
    public String getNombreVisible() {
        String completo = ((nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido)).trim();
        return completo.isEmpty() ? nombreNegocio : completo;
    }
}
