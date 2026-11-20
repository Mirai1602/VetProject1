package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class UsuarioRepository {

    private final EntityManagerFactory emf;

    //Necesita del contructor para que no de error
    //Esto nos permite estar guardando datos
    public UsuarioRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }
    public void GuadarUsuario(Usuario usuario) {
        //Definimos que nuestra variable para manejar los datos sera esta
        EntityManager em = emf.createEntityManager();
        try {
            //Realiza la transaccion de datos, persiste los datos de user y hace commit a los mismo para ultimo luego de verificarlos
            em.getTransaction().begin();
            em.persist(usuario);
            em.getTransaction().commit();
        } catch (Exception ex) {
            //Si todavia no ha pasado del Trnasaction entonces regresa
            if(em.getTransaction().isActive()){
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
            //Y al final por favor cierralo
        }
    }
}
