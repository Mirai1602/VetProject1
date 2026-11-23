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

    //Guarda los cambios de un usuario existente (perfil, foto, contraseña...)
    public Usuario actualizar(Usuario usuario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario actualizado = em.merge(usuario);
            em.getTransaction().commit();
            return actualizado;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    //Busca al usuario por su nombre de usuario (guardado en la columna "negocio")
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.nombreNegocio = :nombre",
                    Usuario.class)
                    .setParameter("nombre", nombreUsuario)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

   //Integracion del metodo que permitira iniciar sesion
    public Usuario buscarPorEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT u from Usuario u where u.email = :email",
                    Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
        }catch ( Exception e){
            return null;
        }finally {
            em.close();
        }
    }
}
