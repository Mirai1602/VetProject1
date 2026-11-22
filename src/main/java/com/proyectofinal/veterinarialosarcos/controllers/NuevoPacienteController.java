package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class NuevoPacienteController {
    @FXML
    private CheckBox chkFechaDesconocida;

    @FXML
    private DatePicker dpFechaNacimiento;

    @FXML
    private TextField txtEdadAproximada;
    @FXML
    private RadioButton rbMacho;

    @FXML
    private RadioButton rbHembra;
    private ToggleGroup sexoGroup;

    @FXML
    private void initialize() {
        sexoGroup = new ToggleGroup();

        rbMacho.setToggleGroup(sexoGroup);
        rbHembra.setToggleGroup(sexoGroup);

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

    private Sexo obtenerSexo() {

        if (rbMacho.isSelected()) {
            return Sexo.MACHO;
        }

        if (rbHembra.isSelected()) {
            return Sexo.HEMBRA;
        }

        return null;
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
