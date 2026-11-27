package com.proyectofinal.veterinarialosarcos.repositories;

import com.proyectofinal.veterinarialosarcos.models.Expediente;
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

    /** Guarda el paciente, su propietario y su expediente clínico (vacío) en una sola transacción. */
    public void guardar(Paciente paciente, Propietario propietario) {
        enTransaccion(em -> {
            em.persist(propietario);
            em.persist(paciente);
            em.persist(new Expediente(paciente));
        });
    }

    /**
     * Actualiza los datos del propietario del paciente (solo si el paciente es del usuario).
     * Si el propietario tiene varias mascotas, el cambio se ve en todas.
     */
    public void actualizarPropietario(Long pacienteId, String nombre, String direccion,
                                      String telefono, int usuarioId) {
        enTransaccion(em -> {
            Paciente paciente = ConsultaRepository.pacienteDelUsuario(em, pacienteId, usuarioId);
            Propietario propietario = paciente.getOwner();
            if (propietario == null) {
                throw new IllegalStateException("Este paciente no tiene propietario registrado.");
            }
            propietario.setNombreOwner(nombre);
            propietario.setDireccion(direccion);
            propietario.setTelefono(telefono);
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
