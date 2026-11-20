package com.proyectofinal.veterinarialosarcos.models;

import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;

import java.time.LocalDate;
import java.time.Period;

/**
 * Datos de un veterinario para la tabla del administrador.
 * No es una entidad: se arma directamente desde la consulta en UsuarioRepository.
 *
 * @param pacientesAtendidos  pacientes cuyas citas ya están todas resueltas
 * @param pacientesAtendiendo resto de sus pacientes (con citas pendientes o aún sin citas)
 */
public record VeterinarioResumen(
        int id,
        String nombres,
        String apellidos,
        String cedula,
        Sexo sexo,
        LocalDate fechaNacimiento,
        String email,
        String telefono,
        Integer numConsultorio,
        String especialidad,
        Long pacientesAtendidos,
        Long pacientesAtendiendo,
        String usuario,
        String password
) {
    /** Edad en años, o null si no se registró la fecha de nacimiento. */
    public Integer edad() {
        return fechaNacimiento == null ? null : Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}
