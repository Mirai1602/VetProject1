package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import com.proyectofinal.veterinarialosarcos.models.Vacuna;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

/** Catálogo de vacunas (se llena con migraciones/R2.sql). */
public class VacunaRepository {

    private final EntityManagerFactory emf;

    public VacunaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /** Vacunas que corresponden a la especie indicada, ordenadas por nombre. */
    public List<Vacuna> listarPorEspecie(Especie especie) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT v FROM Vacuna v WHERE v.especie = :especie ORDER BY v.nombre",
                            Vacuna.class)
                    .setParameter("especie", especie)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
