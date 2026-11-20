package com.proyectofinal.veterinarialosarcos.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    //Maneja la variable que controla el envio de informacion a la base de datos
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("veterinariaPU");
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
