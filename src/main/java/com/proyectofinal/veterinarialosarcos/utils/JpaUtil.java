package com.proyectofinal.veterinarialosarcos.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    private static final String UNIDAD = "veterinariaPU";

    //Maneja la variable que controla el envio de informacion a la base de datos.
    //Al crearse, la base de datos se actualiza sola:
    // 1) migraciones SQL pendientes (V*.sql), 2) Hibernate crea tablas/columnas nuevas,
    // 3) scripts repetibles (R*.sql). Ver MigracionesBD.
    private static final EntityManagerFactory emf = crear();

    private static EntityManagerFactory crear() {
        MigracionesBD.aplicarVersionadas(UNIDAD);
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(UNIDAD);
        MigracionesBD.aplicarRepetibles(factory);
        return factory;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
