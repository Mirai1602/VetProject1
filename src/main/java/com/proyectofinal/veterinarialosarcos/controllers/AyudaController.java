package com.proyectofinal.veterinarialosarcos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AyudaController {

    @FXML
    private void regresar(ActionEvent event) {
        Navegacion.irA(event, "Dashboard.fxml");
    }
}
