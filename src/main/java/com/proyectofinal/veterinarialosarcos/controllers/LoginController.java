package com.proyectofinal.veterinarialosarcos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Button btnCrearCuenta;
    @FXML
    private Button btnIngresarCuenta;

    @FXML
    private void registrar(ActionEvent event) {
        System.out.println("Botón pulsado");
    }

    @FXML
    private void mostrarLogin(ActionEvent event) {
        System.out.println("Botón pulsado");
    }
}
