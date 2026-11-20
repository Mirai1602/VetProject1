package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id  //Auto genera el Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_Paciente",unique = true, nullable = false)
    private String numPaciente;

    @Column(name = "num_Expediente",unique = true, nullable = false)
    private String numExpediente;

    @Column(nullable = false)
    private String nombre;
    @Column(name = "especie")
    private Especie especie;
    private String raza;

    private LocalDate FechaNac;

    private boolean genero;

    public Paciente(Long id, String numPaciente, String numExpediente, String nombre, String raza, Especie especie, LocalDate fechaNac, boolean genero, Propietario propietario) {
        this.id = id;
        this.numPaciente = numPaciente;
        this.numExpediente = numExpediente;
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        FechaNac = fechaNac;
        this.genero = genero;
        this.propietario = propietario;
    }

    public Paciente() {

    }

    public Propietario getOwner() {
        return propietario;
    }

    public void setOwner(Propietario propietario) {
        this.propietario = propietario;
    }

    public boolean isGenero() {
        return genero;
    }

    public void setGenero(boolean genero) {
        this.genero = genero;
    }

    public LocalDate getFechaNac() {
        return FechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        FechaNac = fechaNac;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
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

    public String getNumExpediente() {
        return numExpediente;
    }

    public void setNumExpediente(String numExpediente) {
        this.numExpediente = numExpediente;
    }

    public String getNumPaciente() {
        return numPaciente;
    }

    public void setNumPaciente(String numPaciente) {
        this.numPaciente = numPaciente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "id_owner", nullable = false)
    private Propietario propietario;


    //Metodo para calcular la edad del paciente
    @Transient
    public int getEdadMeses(){
        return Period.between(FechaNac, LocalDate.now()).getMonths();
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }




}

