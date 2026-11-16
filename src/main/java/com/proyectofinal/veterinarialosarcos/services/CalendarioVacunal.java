package com.proyectofinal.veterinarialosarcos.services;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.Vacuna;

import java.util.List;

//Dado un paciente, dime que vacunas le corresponde
//Todas estas lista solo se guardan temporalmente en la memoria, ya que yo no quiero saber cual es
//el inventario de la vet con respecto a la disposicion de las vacunas
//sino que quiero saber cuales vacunas aplicar a cada paciente, entonces solo eso aparecera en la BD
public interface CalendarioVacunal {
    List<Vacuna> ObtenerVacunasDisponibles(Paciente paciente);

}
