package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.Propietario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.function.Consumer;

public class PacienteRepository {

    private final EntityManagerFactory emf;

    public PacienteRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    /** Pacientes del usuario indicado, con su propietario, ordenados por especie y nombre. */
    public List<Paciente> listarPorUsuario(int usuarioId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Paciente p JOIN FETCH p.propietario "
                            + "WHERE p.usuario.id = :usuarioId ORDER BY p.especie, p.nombre",
                    Paciente.class)
                    .setParameter("usuarioId", usuarioId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void guardar(Paciente paciente, Propietario propietario) {
        enTransaccion(em -> {
            em.persist(propietario);
            em.persist(paciente);
        });
    }

    /** Guarda los textos del expediente clínico, solo si el paciente pertenece al usuario. */
    public void actualizarExpediente(Paciente datos, int usuarioId) {
        enTransaccion(em -> {
            Paciente paciente = em.find(Paciente.class, datos.getId());
            if (paciente == null || paciente.getUsuario() == null
                    || paciente.getUsuario().getId() != usuarioId) {
                throw new IllegalStateException("Este paciente no pertenece a tu cuenta.");
            }
            paciente.setHistorialVacunas(datos.getHistorialVacunas());
            paciente.setHistorialDesparasitacion(datos.getHistorialDesparasitacion());
            paciente.setControlParasitos(datos.getControlParasitos());
            paciente.setObservaciones(datos.getObservaciones());
        });
    }

    private void enTransaccion(Consumer<EntityManager> trabajo) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            trabajo.accept(em);
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
