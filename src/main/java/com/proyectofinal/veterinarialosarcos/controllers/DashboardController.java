package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class DashboardController {

    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblBienvenida;
    @FXML
    private ImageView imgPerfil;
    @FXML
    private Label lblInicial;

    @FXML
    private void initialize() {
        imgPerfil.setClip(new Circle(32, 32, 32));
        Usuario usuario = Sesion.getUsuario();
        if (usuario == null) {
            return;
        }
        lblUsuario.setText(usuario.getNombreVisible());
        lblBienvenida.setText("¡Bienvenido de nuevo, " + usuario.getNombreVisible() + "!");
        Image foto = Navegacion.imagen(usuario.getFoto());
        imgPerfil.setImage(foto);
        lblInicial.setVisible(foto == null);
        lblInicial.setText(usuario.getNombreVisible().substring(0, 1).toUpperCase());
    }

    @FXML
    private void abrirPacientes(ActionEvent event) {
        Navegacion.irA(event, "Paciente.fxml");
    }

    @FXML
    private void abrirResumen(ActionEvent event) {
        Navegacion.irA(event, "Resumen.fxml");
    }

    @FXML
    private void abrirAjustes(ActionEvent event) {
        Navegacion.irA(event, "Ajustes.fxml");
    }

    @FXML
    private void abrirAyuda(ActionEvent event) {
        Navegacion.irA(event, "Ayuda.fxml");
    }

    @FXML
    private void regresar(ActionEvent event) {
        cerrarSesion(event);
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Sesion.cerrar();
        Navegacion.irA(event, "Login.fxml");
    }
}
