package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.services.UsuarioService;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController {

    private static final int MIN_USUARIO = 3;
    private static final int MIN_PASSWORD = 6;

    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Label lblMensaje;

    @FXML
    private void registrar(ActionEvent event) {
        Validacion.limpiar(lblMensaje, txtUsuario, txtCorreo, txtPassword, txtConfirmPassword);

        if (Validacion.estaVacio(txtUsuario)) {
            Validacion.error(lblMensaje, txtUsuario, "Ingresa un nombre de usuario.");
            return;
        }
        if (txtUsuario.getText().trim().length() < MIN_USUARIO) {
            Validacion.error(lblMensaje, txtUsuario,
                    "El nombre de usuario debe tener al menos " + MIN_USUARIO + " caracteres.");
            return;
        }
        if (Validacion.estaVacio(txtCorreo)) {
            Validacion.error(lblMensaje, txtCorreo, "Ingresa tu correo electrónico.");
            return;
        }
        if (!Validacion.esCorreoValido(txtCorreo.getText())) {
            Validacion.error(lblMensaje, txtCorreo, "El correo electrónico no es válido.");
            return;
        }
        if (Validacion.estaVacio(txtPassword)) {
            Validacion.error(lblMensaje, txtPassword, "Ingresa una contraseña.");
            return;
        }
        if (txtPassword.getText().length() < MIN_PASSWORD) {
            Validacion.error(lblMensaje, txtPassword,
                    "La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres.");
            return;
        }
        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            Validacion.error(lblMensaje, txtConfirmPassword, "Las contraseñas no coinciden.");
            return;
        }

        // TODO: guardar el usuario en la base de datos.
        UsuarioRepository repository =
                new UsuarioRepository(JpaUtil.getEntityManager().getEntityManagerFactory());
        UsuarioService service = new UsuarioService(repository);
        service.ResgiatrarUsuario(
                txtUsuario.getText(),
                txtCorreo.getText(),
                txtPassword.getText()

        );
        Validacion.exito(lblMensaje, "¡Cuenta creada! Ya puedes iniciar sesión.");
    }

    @FXML
    private void mostrarLogin(ActionEvent event) {
        Navegacion.irA(event, "Login.fxml");
    }
}
