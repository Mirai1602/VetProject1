package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.models.Consulta;
import com.proyectofinal.veterinarialosarcos.models.Expediente;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.Vacuna;
import com.proyectofinal.veterinarialosarcos.models.VacunaAplicada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

/**
 * Consultas (visitas) de los pacientes. Solo se crean consultas nuevas: una consulta
 * guardada nunca se modifica, para que el historial clínico no se pierda.
 */
public class ConsultaRepository {

    private final EntityManagerFactory emf;

    public ConsultaRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /** Consultas del paciente, de la más reciente a la más antigua. */
    public List<Consulta> listarPorPaciente(Long pacienteId, int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            pacienteDelUsuario(em, pacienteId, usuarioId);
            return em.createQuery(
                            "SELECT c FROM Consulta c WHERE c.paciente.id = :pacienteId "
                                    + "ORDER BY c.fecha DESC, c.id DESC", Consulta.class)
                    .setParameter("pacienteId", pacienteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Consulta completa (con sus vacunas aplicadas), solo si es de un paciente del usuario. */
    public Consulta buscar(Long consultaId, int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            Consulta consulta = em.createQuery(
                            "SELECT DISTINCT c FROM Consulta c "
                                    + "LEFT JOIN FETCH c.vacunas va LEFT JOIN FETCH va.vacuna "
                                    + "JOIN FETCH c.paciente p "
                                    + "WHERE c.id = :id AND p.usuario.id = :usuarioId", Consulta.class)
                    .setParameter("id", consultaId)
                    .setParameter("usuarioId", usuarioId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (consulta == null) {
                throw new IllegalStateException("Esta consulta no pertenece a tu cuenta.");
            }
            return consulta;
        } finally {
            em.close();
        }
    }

    /**
     * Registra una consulta NUEVA para el paciente, con sus vacunas aplicadas.
     * Si el paciente aún no tenía expediente (carpeta), se le crea.
     */
    public void registrar(Long pacienteId, Consulta consulta, int usuarioId) {
        if (consulta.getId() != null) {
            throw new IllegalArgumentException("Las consultas guardadas no se modifican; registra una nueva.");
        }
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Paciente paciente = pacienteDelUsuario(em, pacienteId, usuarioId);
            asegurarExpediente(em, paciente);
            consulta.setPaciente(paciente);
            for (VacunaAplicada aplicada : consulta.getVacunas()) {
                aplicada.setVacuna(em.getReference(Vacuna.class, aplicada.getVacuna().getId()));
            }
            em.persist(consulta);
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

    private static void asegurarExpediente(EntityManager em, Paciente paciente) {
        boolean existe = !em.createQuery(
                        "SELECT e.id FROM Expediente e WHERE e.paciente.id = :pacienteId", Long.class)
                .setParameter("pacienteId", paciente.getId())
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
        if (!existe) {
            em.persist(new Expediente(paciente));
        }
    }

    static Paciente pacienteDelUsuario(EntityManager em, Long pacienteId, int usuarioId) {
        Paciente paciente = em.find(Paciente.class, pacienteId);
        if (paciente == null || paciente.getUsuario() == null
                || paciente.getUsuario().getId() != usuarioId) {
            throw new IllegalStateException("Este paciente no pertenece a tu cuenta.");
        }
        return paciente;
    }
}
