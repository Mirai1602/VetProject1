package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.enumerados.EstadoCaso;
import com.proyectofinal.veterinarialosarcos.enumerados.Rol;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.models.VeterinarioResumen;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

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

   //Integracion del metodo que permitira iniciar sesion (busca por nombre de usuario)
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT u from Usuario u where u.nombreNegocio = :nombre",
                    Usuario.class)
                    .setParameter("nombre", nombreUsuario)
                    .getSingleResult();
        }catch ( Exception e){
            return null;
        }finally {
            em.close();
        }
    }

    //Un paciente está "atendido" si tiene al menos una cita resuelta y ninguna pendiente
    private static final String PACIENTE_ATENDIDO =
            "EXISTS (SELECT c FROM Citas c WHERE c.paciente = p AND c.estado = :resuelto) " +
            "AND NOT EXISTS (SELECT c FROM Citas c WHERE c.paciente = p AND c.estado <> :resuelto)";

    //Veterinarios con sus datos y el número de pacientes atendidos / atendiendo, para la vista del admin
    public List<VeterinarioResumen> listarVeterinarios() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.proyectofinal.veterinarialosarcos.models.VeterinarioResumen(" +
                    "  u.id, u.nombres, u.apellidos, u.cedula, u.sexo, u.fechaNacimiento, u.email, u.telefono," +
                    "  u.numConsultorio, u.especialidad," +
                    "  (SELECT COUNT(p) FROM Paciente p WHERE p.usuario = u AND " + PACIENTE_ATENDIDO + ")," +
                    "  (SELECT COUNT(p) FROM Paciente p WHERE p.usuario = u AND NOT (" + PACIENTE_ATENDIDO + "))," +
                    "  u.nombreNegocio, u.password) " +
                    "FROM Usuario u " +
                    "WHERE u.rol = :rol " +
                    "ORDER BY u.apellidos, u.nombres",
                    VeterinarioResumen.class)
                    .setParameter("rol", Rol.VETERINARIO)
                    .setParameter("resuelto", EstadoCaso.RESUELTO)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario buscarPorId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    //Copia los datos editables del veterinario (incluidos usuario y contraseña) sobre el registro existente
    public void actualizar(int id, Usuario datos) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            usuario.setNombreNegocio(datos.getNombreNegocio());
            usuario.setEmail(datos.getEmail());
            usuario.setPassword(datos.getPassword());
            usuario.setNombres(datos.getNombres());
            usuario.setApellidos(datos.getApellidos());
            usuario.setSexo(datos.getSexo());
            usuario.setFechaNacimiento(datos.getFechaNacimiento());
            usuario.setTelefono(datos.getTelefono());
            usuario.setCedula(datos.getCedula());
            usuario.setNumConsultorio(datos.getNumConsultorio());
            usuario.setEspecialidad(datos.getEspecialidad());
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public long contarPacientes(int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT COUNT(p) FROM Paciente p WHERE p.usuario.id = :id", Long.class)
                    .setParameter("id", usuarioId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    //Solo borra veterinarios; el admin nunca se elimina desde aquí
    public void eliminarVeterinario(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Usuario usuario = em.find(Usuario.class, id);
            if (usuario != null && usuario.getRol() == Rol.VETERINARIO) {
                em.remove(usuario);
            }
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    //Se usa para no repetir cédulas (la columna cedula es unique)
    public Usuario buscarPorCedula(String cedula) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT u from Usuario u where u.cedula = :cedula", Usuario.class)
                    .setParameter("cedula", cedula)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    //Se usa al registrar para no repetir correos (la columna email es unique)
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
