package com.proyectofinal.veterinarialosarcos.services;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.PacienteResumen;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;

import java.util.List;

public class PacienteService {
    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    //Cada veterinaria (usuario) solo ve sus propios pacientes
    public List<PacienteResumen> listarPacientes(Usuario usuario) {
        return repository.listarResumenPorUsuario(usuario.getId());
    }

    public Paciente obtenerPaciente(Long id) {
        return repository.buscarPorId(id);
    }

    public boolean expedienteEnUso(String numExpediente, Long idPacienteActual) {
        return repository.existeExpediente(numExpediente, idPacienteActual);
    }

    //id null = paciente nuevo; con id = actualizar el existente
    public void guardarPaciente(Long id, Paciente datos, String nombreDueno, String telefono, Usuario usuario) {
        repository.guardar(id, datos, nombreDueno, telefono, usuario.getId());
    }

    public void eliminarPaciente(Long id) {
        repository.eliminar(id);
    }
}
