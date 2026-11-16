package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import com.proyectofinal.veterinarialosarcos.models.Vacuna;

import java.util.List;

public interface VacunaRepository {
    List<Vacuna> ObtenerTodas();
    List<Vacuna> ObtenerTodasPosEspecie(Especie especie);
}
