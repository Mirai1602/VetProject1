package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Cambia la vista mostrada en la ventana actual reemplazando la raíz de la escena.
 */
final class Navegacion {

    private Navegacion() {
    }

    static void irA(ActionEvent event, String fxml) {
        try {
            Parent vista = FXMLLoader.load(HelloApplication.class.getResource(fxml));
            ((Node) event.getSource()).getScene().setRoot(vista);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar la vista " + fxml, e);
        }
    }
}
