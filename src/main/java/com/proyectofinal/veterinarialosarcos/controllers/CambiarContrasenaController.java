package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.services.UsuarioService;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

/**
 * Cambio de contraseña en dos pasos: primero se verifica la contraseña actual y,
 * solo si es correcta, se permite escribir la nueva.
 */
public class CambiarContrasenaController {

    private static final int MIN_PASSWORD = 6;
    private static final int MAX_INTENTOS = 3;

    @FXML
    private VBox pasoVerificar;
    @FXML
    private VBox pasoNueva;
    @FXML
    private PasswordField txtActual;
    @FXML
    private PasswordField txtNueva;
    @FXML
    private PasswordField txtConfirmar;
    @FXML
    private Label lblMensaje;

    private final UsuarioService service =
            new UsuarioService(new UsuarioRepository(JpaUtil.getEntityManagerFactory()));
    private int intentos;
    private String actualVerificada;

    @FXML
    private void verificar(ActionEvent event) {
        Validacion.limpiar(lblMensaje, txtActual);
        if (Validacion.estaVacio(txtActual)) {
            Validacion.error(lblMensaje, txtActual, "Ingresa tu contraseña actual.");
            return;
        }
        if (!service.contrasenaCorrecta(Sesion.getUsuario(), txtActual.getText())) {
            intentos++;
            txtActual.clear();
            if (intentos >= MAX_INTENTOS) {
                Navegacion.alerta(Alert.AlertType.WARNING, "Demasiados intentos",
                        "La contraseña fue incorrecta " + MAX_INTENTOS + " veces. Inténtalo más tarde.");
                Navegacion.cerrarVentana(event);
                return;
            }
            Validacion.error(lblMensaje, txtActual, "La contraseña actual no es correcta. Te quedan "
                    + (MAX_INTENTOS - intentos) + " intento(s).");
            return;
        }
        actualVerificada = txtActual.getText();
        txtActual.clear();
        mostrar(pasoVerificar, false);
        mostrar(pasoNueva, true);
        txtNueva.requestFocus();
    }

    @FXML
    private void guardar(ActionEvent event) {
        Validacion.limpiar(lblMensaje, txtNueva, txtConfirmar);
        String nueva = txtNueva.getText();
        if (nueva == null || nueva.length() < MIN_PASSWORD) {
            Validacion.error(lblMensaje, txtNueva,
                    "La nueva contraseña debe tener al menos " + MIN_PASSWORD + " caracteres.");
            return;
        }
        if (nueva.equals(actualVerificada)) {
            Validacion.error(lblMensaje, txtNueva, "La nueva contraseña debe ser distinta a la actual.");
            return;
        }
        if (!nueva.equals(txtConfirmar.getText())) {
            Validacion.error(lblMensaje, txtConfirmar, "Las contraseñas no coinciden.");
            return;
        }
        try {
            Usuario actualizado = service.cambiarContrasena(Sesion.getUsuario(), nueva);
            Sesion.iniciar(actualizado);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Validacion.error(lblMensaje, txtNueva, "No se pudo cambiar la contraseña.");
            return;
        }
        Navegacion.alerta(Alert.AlertType.INFORMATION, "Contraseña actualizada",
                "Tu contraseña se cambió correctamente.");
        Navegacion.cerrarVentana(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }

    private static void mostrar(VBox paso, boolean visible) {
        paso.setVisible(visible);
        paso.setManaged(visible);
        paso.setDisable(!visible); // así Enter no activa los botones del paso oculto
    }
}
