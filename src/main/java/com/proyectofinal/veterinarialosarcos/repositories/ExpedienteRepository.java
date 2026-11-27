package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.models.Expediente;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class ExpedienteRepository {

    private final EntityManagerFactory emf;

    public ExpedienteRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /**
     * Expediente del paciente indicado. Si todavía no tiene uno guardado, devuelve uno
     * nuevo vacío (sin guardar). Solo si el paciente pertenece al usuario.
     */
    public Expediente obtenerPorPaciente(Long pacienteId, int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            Paciente paciente = pacienteDelUsuario(em, pacienteId, usuarioId);
            Expediente expediente = buscar(em, pacienteId);
            return expediente != null ? expediente : new Expediente(paciente);
        } finally {
            em.close();
        }
    }

    /** Guarda los textos del expediente clínico; lo crea si el paciente aún no tenía. */
    public void guardar(Long pacienteId, Expediente datos, int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Paciente paciente = pacienteDelUsuario(em, pacienteId, usuarioId);
            Expediente expediente = buscar(em, pacienteId);
            if (expediente == null) {
                expediente = new Expediente(paciente);
                em.persist(expediente);
            }
            expediente.setHistorialVacunas(datos.getHistorialVacunas());
            expediente.setHistorialDesparasitacion(datos.getHistorialDesparasitacion());
            expediente.setControlParasitos(datos.getControlParasitos());
            expediente.setObservaciones(datos.getObservaciones());
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

    private static Paciente pacienteDelUsuario(EntityManager em, Long pacienteId, int usuarioId) {
        Paciente paciente = em.find(Paciente.class, pacienteId);
        if (paciente == null || paciente.getUsuario() == null
                || paciente.getUsuario().getId() != usuarioId) {
            throw new IllegalStateException("Este paciente no pertenece a tu cuenta.");
        }
        return paciente;
    }

    private static Expediente buscar(EntityManager em, Long pacienteId) {
        return em.createQuery(
                        "SELECT e FROM Expediente e WHERE e.paciente.id = :pacienteId", Expediente.class)
                .setParameter("pacienteId", pacienteId)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
