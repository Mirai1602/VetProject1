package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Rol;
import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Nombre de usuario con el que se inicia sesión (la columna conserva su nombre original)
    @Column(name = "negocio", nullable = false)
    private String nombreNegocio;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "contrasenia", nullable = false)
    private String password;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    //ADMIN (solo uno) o VETERINARIO
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private Rol rol = Rol.VETERINARIO;

    private String nombres;

    private String apellidos;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String telefono;

    //Cédula: formato 001-220807-1019M, 16 caracteres con guiones (se valida en el formulario)
    @Column(name = "cedula", length = 16, unique = true)
    private String cedula;

    @Column(name = "num_consultorio")
    private Integer numConsultorio;

    private String especialidad;

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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Integer getNumConsultorio() {
        return numConsultorio;
    }

    public void setNumConsultorio(Integer numConsultorio) {
        this.numConsultorio = numConsultorio;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    //Nombre para mostrar: "Nombres Apellidos" o, si no tiene, el nombre de usuario
    public String getNombreParaMostrar() {
        if (nombres == null || nombres.isBlank()) {
            return nombreNegocio;
        }
        return apellidos == null ? nombres : nombres + " " + apellidos;
    }

    @OneToMany(mappedBy = "usuario")
    private List<Paciente> pacientes;

}
