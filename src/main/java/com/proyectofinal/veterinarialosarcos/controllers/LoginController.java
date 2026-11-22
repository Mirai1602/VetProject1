package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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

        //UsuarioRepository repository =
                //new UsuarioRepository(JpaUtil.getEntityManager().getEntityManagerFactory());
        //Validacion.exito(lblMensaje, "¡Bienvenido, " + txtUsuario.getText().trim() + "!");

        Navegacion.irA(event, "Dashboard.fxml");
    }



    @FXML
    private void mostrarRegistro(ActionEvent event) {
        Navegacion.irA(event, "Registro.fxml");
    }
}
