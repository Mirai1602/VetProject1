package com.proyectofinal.veterinarialosarcos.models;

import java.time.LocalDateTime;

/**
 * Información general de un paciente para mostrarla en la tabla principal.
 * No es una entidad: se arma directamente desde la consulta en PacienteRepository.
 *
 * @param ultimaCita null si el paciente todavía no tiene citas
 */
public record PacienteResumen(
        Long id,
        String nombre,
        String dueno,
        LocalDateTime ultimaCita,
        String expediente
) {
}
