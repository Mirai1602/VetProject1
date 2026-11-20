package com.proyectofinal.veterinarialosarcos.services;

import com.proyectofinal.veterinarialosarcos.enumerados.Rol;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.models.VeterinarioResumen;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {
    private final UsuarioRepository repository;
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    //Solo el admin registra usuarios, y siempre como VETERINARIO (el admin es único)
    //"datos" trae lo que se llenó en el formulario; el rol y la fecha de creación los pone el sistema
    public void registrarVeterinario(Usuario datos) {
        datos.setFechaCreacion(LocalDate.now());
        datos.setRol(Rol.VETERINARIO);

        //Invoca al metodo de GuardarUsuario desde aca, el Service.
        repository.GuadarUsuario(datos);
    }

    public boolean autenticarUsuario(String nombreUsuario, String password){
        return iniciarSesion(nombreUsuario, password) != null;
    }

    //Devuelve el usuario si las credenciales son correctas, o null si no lo son
    public Usuario iniciarSesion(String nombreUsuario, String password){
        Usuario usuario = repository.buscarPorNombreUsuario(nombreUsuario);
        if(usuario == null || !usuario.getPassword().equals(password)){
            return null;
        }
        return usuario;
    }

    public boolean nombreUsuarioEnUso(String nombreUsuario) {
        return nombreUsuarioEnUso(nombreUsuario, null);
    }

    public boolean emailEnUso(String email) {
        return emailEnUso(email, null);
    }

    //idActual = el veterinario que se está editando (puede conservar su propio usuario/correo)
    public boolean nombreUsuarioEnUso(String nombreUsuario, Integer idActual) {
        Usuario existente = repository.buscarPorNombreUsuario(nombreUsuario);
        return existente != null && (idActual == null || existente.getId() != idActual);
    }

    public boolean emailEnUso(String email, Integer idActual) {
        Usuario existente = repository.buscarPorEmail(email);
        return existente != null && (idActual == null || existente.getId() != idActual);
    }

    public Usuario obtenerUsuario(int id) {
        return repository.buscarPorId(id);
    }

    public boolean cedulaEnUso(String cedula, Integer idActual) {
        Usuario existente = repository.buscarPorCedula(cedula);
        return existente != null && (idActual == null || existente.getId() != idActual);
    }

    public void actualizarVeterinario(int id, Usuario datos) {
        repository.actualizar(id, datos);
    }

    public long pacientesAsignados(int veterinarioId) {
        return repository.contarPacientes(veterinarioId);
    }

    public void eliminarVeterinario(int id) {
        repository.eliminarVeterinario(id);
    }

    public List<VeterinarioResumen> listarVeterinarios() {
        return repository.listarVeterinarios();
    }

}
