package com.proyectofinal.veterinarialosarcos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.annotations.Parent;

public class PacienteController {

    @FXML
    private void nuevoPaciente(ActionEvent event) {
        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/proyectofinal/veterinarialosarcos/NuevoPaciente.fxml"
                            )
                    );

            Scene scene =
                    new Scene(loader.load());

            Stage stage = new Stage();

            stage.setTitle("Nuevo Paciente");

            stage.setScene(scene);

            stage.showAndWait();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}




