package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class NuevoPacienteController {
    @FXML
    private CheckBox chkFechaDesconocida;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private TextField txtEdadAproximada;

    @FXML
    private void initialize() {

        chkFechaDesconocida.selectedProperty().addListener(
                (obs, anterior, seleccionado) -> {

                    dpFechaNacimiento.setDisable(seleccionado);

                    txtEdadAproximada.setDisable(!seleccionado);

                    if(seleccionado) {
                        dpFechaNacimiento.setValue(null);
                    }
                }
        );
    }

    @FXML
    private void guardarPaciente() {
        Paciente paciente = new Paciente();
    }

    @FXML
    private void cancelar() {
        System.out.println("Cancelando");
    }
}
