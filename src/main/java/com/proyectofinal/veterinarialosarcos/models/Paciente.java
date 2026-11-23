package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "especie")
    private Especie especie;
    private String raza;

    public Paciente(Sexo sexo) {
        this.sexo = sexo;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    private LocalDate FechaNac;
    private Integer edad;


    public Paciente(boolean fechaNacimientoConocida) {
        this.fechaNacimientoConocida = fechaNacimientoConocida;
    }

    public boolean isFechaNacimientoConocida() {
        return Boolean.TRUE.equals(fechaNacimientoConocida);
    }

    public void setFechaNacimientoConocida(boolean fechaNacimientoConocida) {
        this.fechaNacimientoConocida = fechaNacimientoConocida;
    }

    // Boolean (no boolean) para que la columna admita null y Hibernate pueda
    // agregarla aunque la tabla ya tenga filas.
    private Boolean fechaNacimientoConocida;


    public Paciente(Long id, String numPaciente, String numExpediente, String nombre, String raza, Especie especie, LocalDate fechaNac,Integer Edad,  Propietario propietario) {
        this.id = id;
        this.numPaciente = numPaciente;
        this.numExpediente = numExpediente;
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        FechaNac = fechaNac;
        this.edad = Edad;
        //Se elemina lo de boolean genero porque ya estaba dos veces
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


    //Acá se eliminaron dos cosas que hacían referencia a boolean Sexo

    public LocalDate getFechaNac() {
        return FechaNac;
    }

    public void setFechaNac(LocalDate fechaNac) {
        FechaNac = fechaNac;
    }
    public Integer getEdad() {
        return edad;
    }
    public void setEdad(Integer edad) {
        this.edad = edad;
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
    public int getEdadMeses(){
        if (FechaNac == null) {
            return edad == null ? 0 : edad * 12;
        }
        return (int) Period.between(FechaNac, LocalDate.now()).toTotalMonths();
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    //Datos del expediente clínico
    @Column(name = "historial_vacunas", columnDefinition = "text")
    private String historialVacunas;

    @Column(name = "historial_desparasitacion", columnDefinition = "text")
    private String historialDesparasitacion;

    @Column(name = "control_parasitos", columnDefinition = "text")
    private String controlParasitos;

    @Column(name = "observaciones", columnDefinition = "text")
    private String observaciones;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getHistorialVacunas() {
        return historialVacunas;
    }

    public void setHistorialVacunas(String historialVacunas) {
        this.historialVacunas = historialVacunas;
    }

    public String getHistorialDesparasitacion() {
        return historialDesparasitacion;
    }

    public void setHistorialDesparasitacion(String historialDesparasitacion) {
        this.historialDesparasitacion = historialDesparasitacion;
    }

    public String getControlParasitos() {
        return controlParasitos;
    }

    public void setControlParasitos(String controlParasitos) {
        this.controlParasitos = controlParasitos;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /** Edad legible, p. ej. "2 años 3 meses". */
    public String getEdadTexto() {
        int meses = getEdadMeses();
        if (FechaNac == null && edad == null) {
            return "Sin dato";
        }
        int anios = meses / 12;
        int resto = meses % 12;
        String texto = anios > 0 ? anios + (anios == 1 ? " año" : " años") : "";
        if (resto > 0) {
            texto += (texto.isEmpty() ? "" : " ") + resto + (resto == 1 ? " mes" : " meses");
        }
        if (texto.isEmpty()) {
            texto = "Menos de 1 mes";
        }
        return FechaNac == null ? "≈ " + texto : texto;
    }

    /** Nombre de la especie con mayúscula inicial, p. ej. "Perro". */
    public String getEspecieTexto() {
        if (especie == null) {
            return "Sin especie";
        }
        String n = especie.name().toLowerCase();
        return Character.toUpperCase(n.charAt(0)) + n.substring(1);
    }

    public String getSexoTexto() {
        if (sexo == null) {
            return "—";
        }
        return sexo == Sexo.MACHO ? "Macho" : "Hembra";
    }




}

