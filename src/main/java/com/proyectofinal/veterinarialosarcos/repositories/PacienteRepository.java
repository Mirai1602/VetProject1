package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.PacienteResumen;
import com.proyectofinal.veterinarialosarcos.models.Propietario;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.UUID;

public class PacienteRepository {

    private final EntityManagerFactory emf;

    public PacienteRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    //Trae los pacientes del usuario (veterinaria) con su dueño, la fecha de su última cita y su expediente
    public List<PacienteResumen> listarResumenPorUsuario(int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT new com.proyectofinal.veterinarialosarcos.models.PacienteResumen(" +
                    "  p.id, p.nombre, o.nombreOwner," +
                    "  (SELECT MAX(c.fechaHora) FROM Citas c WHERE c.paciente = p)," +
                    "  p.numExpediente) " +
                    "FROM Paciente p JOIN p.propietario o " +
                    "WHERE p.usuario.id = :usuarioId " +
                    "ORDER BY p.nombre",
                    PacienteResumen.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    //Trae un paciente con su dueño ya cargado (para llenar el formulario de edición)
    public Paciente buscarPorId(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Paciente p JOIN FETCH p.propietario WHERE p.id = :id",
                    Paciente.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    //true si otro paciente (distinto de idExcluido) ya usa ese número de expediente
    public boolean existeExpediente(String numExpediente, Long idExcluido) {
        EntityManager em = emf.createEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM Paciente p WHERE p.numExpediente = :exp";
            if (idExcluido != null) {
                jpql += " AND p.id <> :id";
            }
            var query = em.createQuery(jpql, Long.class).setParameter("exp", numExpediente);
            if (idExcluido != null) {
                query.setParameter("id", idExcluido);
            }
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Crea (id null) o actualiza un paciente en una sola transacción.
     * El dueño se busca por nombre y teléfono; si no existe se crea.
     */
    public void guardar(Long id, Paciente datos, String nombreDueno, String telefono, int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Propietario dueno = em.createQuery(
                    "SELECT o FROM Propietario o WHERE o.nombreOwner = :nombre AND o.telefono = :tel",
                    Propietario.class)
                    .setParameter("nombre", nombreDueno)
                    .setParameter("tel", telefono)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (dueno == null) {
                dueno = new Propietario();
                dueno.setNombreOwner(nombreDueno);
                dueno.setTelefono(telefono);
                em.persist(dueno);
            }

            Paciente paciente = (id == null) ? new Paciente() : em.find(Paciente.class, id);
            paciente.setNombre(datos.getNombre());
            paciente.setEspecie(datos.getEspecie());
            paciente.setRaza(datos.getRaza());
            paciente.setFechaNac(datos.getFechaNac());
            paciente.setGenero(datos.isGenero());
            paciente.setNumExpediente(datos.getNumExpediente());
            paciente.setOwner(dueno);
            paciente.setUsuario(em.getReference(Usuario.class, usuarioId));

            if (id == null) {
                //El número de paciente es interno, se genera solo
                paciente.setNumPaciente("P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                em.persist(paciente);
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

    //Elimina el paciente junto con sus citas (si no, la llave foránea lo impide)
    public void eliminar(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Citas c WHERE c.paciente.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            Paciente paciente = em.find(Paciente.class, id);
            if (paciente != null) {
                em.remove(paciente);
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
}
