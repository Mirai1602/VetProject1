package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.services.UsuarioService;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Label lblMensaje;

    @FXML
    private void iniciarSesion(ActionEvent event) {
        Validacion.limpiar(lblMensaje, txtUsuario, txtPassword);

        if (Validacion.estaVacio(txtUsuario)) {
            Validacion.error(lblMensaje, txtUsuario, "Ingresa tu nombre de usuario.");
            return;
        }
        if (Validacion.estaVacio(txtPassword)) {
            Validacion.error(lblMensaje, txtPassword, "Ingresa tu contraseña.");
            return;
        }

        Usuario usuario;
        try {
            UsuarioService service =
                    new UsuarioService(new UsuarioRepository(JpaUtil.getEntityManagerFactory()));
            usuario = service.iniciarSesion(txtUsuario.getText().trim(), txtPassword.getText());
        } catch (RuntimeException | LinkageError ex) {
            // LinkageError: JpaUtil no pudo crear la conexión al cargarse la clase
            Validacion.error(lblMensaje, txtUsuario, "No se pudo conectar con la base de datos.");
            return;
        }

        if (usuario == null) {
            txtPassword.clear();
            Validacion.error(lblMensaje, txtPassword, "Usuario o contraseña incorrectos.");
            return;
        }

        Sesion.iniciar(usuario);
        // El admin ve la gestión de veterinarios; cada veterinario ve sus pacientes
        Navegacion.irA(event, usuario.esAdmin() ? "Admin.fxml" : "Pacientes.fxml");
    }
}
