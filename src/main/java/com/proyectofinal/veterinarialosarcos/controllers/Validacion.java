package com.proyectofinal.veterinarialosarcos.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;

import java.util.regex.Pattern;

/**
 * Utilidades compartidas para validar formularios y mostrar mensajes al usuario.
 */
final class Validacion {

    private static final Pattern CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private Validacion() {
    }

    static boolean estaVacio(TextInputControl campo) {
        return campo.getText() == null || campo.getText().isBlank();
    }

    static boolean esCorreoValido(String correo) {
        return CORREO.matcher(correo.trim()).matches();
    }

    /** Quita el borde rojo de los campos y oculta el mensaje. */
    static void limpiar(Label mensaje, TextInputControl... campos) {
        for (TextInputControl campo : campos) {
            campo.getStyleClass().remove("input-error");
        }
        mensaje.setVisible(false);
        mensaje.setManaged(false);
    }

    /** Marca el campo en rojo, le da el foco y muestra el error. */
    static void error(Label mensaje, TextInputControl campo, String texto) {
        if (!campo.getStyleClass().contains("input-error")) {
            campo.getStyleClass().add("input-error");
        }
        campo.requestFocus();
        mostrar(mensaje, texto, "mensaje-error");
    }

    static void exito(Label mensaje, String texto) {
        mostrar(mensaje, texto, "mensaje-exito");
    }

    private static void mostrar(Label mensaje, String texto, String estilo) {
        mensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        mensaje.getStyleClass().add(estilo);
        mensaje.setText(texto);
        mensaje.setVisible(true);
        mensaje.setManaged(true);
    }
}
