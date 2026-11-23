package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.HelloApplication;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Consumer;

/**
 * Utilidades de navegación entre vistas y ventanas.
 */
final class Navegacion {

    private Navegacion() {
    }

    /**
     * Cambia la vista mostrada en la ventana actual. Crea una escena nueva para que la
     * ventana adopte el tamaño preferido de cada vista.
     */
    static void irA(ActionEvent event, String fxml) {
        Parent vista = cargar(fxml).getRoot();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(vista));
        if (!stage.isMaximized()) {
            stage.sizeToScene();
            stage.centerOnScreen();
        }
    }

    /**
     * Abre una ventana modal encima de la actual y espera a que se cierre.
     * {@code preparar} recibe el controlador antes de mostrarla (para pasarle datos).
     */
    static <T> void abrirModal(Node origen, String fxml, String titulo, Consumer<T> preparar) {
        FXMLLoader loader = cargar(fxml);
        if (preparar != null) {
            preparar.accept(loader.getController());
        }
        Stage stage = new Stage();
        stage.setTitle(titulo);
        stage.setScene(new Scene(loader.getRoot()));
        stage.initOwner(origen.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.showAndWait();
    }

    static void abrirExpediente(Node origen, Paciente paciente) {
        Navegacion.<ExpedienteController>abrirModal(origen, "Expediente.fxml",
                "Expediente de " + paciente.getNombre(), c -> c.setPaciente(paciente));
    }

    static void cerrarVentana(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    static void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }

    static Image imagen(byte[] bytes) {
        return bytes == null || bytes.length == 0 ? null : new Image(new ByteArrayInputStream(bytes));
    }

    private static FXMLLoader cargar(String fxml) {
        URL recurso = HelloApplication.class.getResource(fxml);
        if (recurso == null) {
            throw new IllegalArgumentException("No existe la vista " + fxml);
        }
        FXMLLoader loader = new FXMLLoader(recurso);
        try {
            loader.load();
            return loader;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar la vista " + fxml, e);
        }
    }
}
