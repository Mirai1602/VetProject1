package com.proyectofinal.veterinarialosarcos.services;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.utils.Contrasenas;

import java.time.LocalDate;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ResgiatrarUsuario(String nombreUsuario, String email, String password) {
        Usuario usuario = new Usuario();
        usuario.setNombreNegocio(nombreUsuario);
        usuario.setEmail(email);
        //La contraseña nunca se guarda en texto plano
        usuario.setPassword(Contrasenas.cifrar(password));
        usuario.setFechaCreacion(LocalDate.now());
        repository.GuadarUsuario(usuario);
    }

    /**
     * Devuelve el usuario si el nombre y la contraseña son correctos, o null si no.
     * Si la cuenta es antigua y tenía la contraseña sin cifrar, se cifra en este momento.
     */
    public Usuario autenticarUsuario(String nombreUsuario, String password) {
        Usuario usuario = repository.buscarPorNombreUsuario(nombreUsuario);
        if (usuario == null || !Contrasenas.verificar(password, usuario.getPassword())) {
            return null;
        }
        if (!Contrasenas.estaCifrada(usuario.getPassword())) {
            usuario.setPassword(Contrasenas.cifrar(password));
            usuario = repository.actualizar(usuario);
        }
        return usuario;
    }

    public boolean contrasenaCorrecta(Usuario usuario, String password) {
        return Contrasenas.verificar(password, usuario.getPassword());
    }

    public Usuario cambiarContrasena(Usuario usuario, String nueva) {
        usuario.setPassword(Contrasenas.cifrar(nueva));
        return repository.actualizar(usuario);
    }
}
