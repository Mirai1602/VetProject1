package com.proyectofinal.veterinarialosarcos.services;

import javafx.fxml.FXML;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;

import java.time.LocalDate;

public class UsuarioService {
    private final UsuarioRepository repository;
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void ResgiatrarUsuario(String nombreNegocio, String email, String password){

        Usuario usuario = new Usuario();
        //Solo seteamos cada uno de los datos previos

        usuario.setNombreNegocio(nombreNegocio);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setFechaCreacion(LocalDate.now());

        //Invoca al metodo de GuardarUsuario desde aca, el Service.
        repository.GuadarUsuario(usuario);

    }

}
