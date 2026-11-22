package com.proyectofinal.veterinarialosarcos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    private void abrirPacientes(ActionEvent event) {

        Navegacion.irA(event,"Paciente.fxml");

        // Aquí cargarás Pacientes.fxml
    }

    @FXML
    private void abrirAjustes(ActionEvent event) {

        Navegacion.irA(event,"Ajustes.fxml");
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Navegacion.irA(event,"Login.fxml");

    }
}
