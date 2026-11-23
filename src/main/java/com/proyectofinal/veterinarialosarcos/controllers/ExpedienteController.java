package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ExpedienteController {

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblNumero;
    @FXML
    private Label lblPaciente;
    @FXML
    private Label lblEspecie;
    @FXML
    private Label lblRaza;
    @FXML
    private Label lblSexo;
    @FXML
    private Label lblEdad;
    @FXML
    private Label lblPropietario;
    @FXML
    private Label lblTelefono;
    @FXML
    private TextArea txtVacunas;
    @FXML
    private TextArea txtDesparasitacion;
    @FXML
    private TextArea txtParasitos;
    @FXML
    private TextArea txtObservaciones;

    private Paciente paciente;

    void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        lblTitulo.setText("Expediente Clínico · " + paciente.getNombre());
        lblNumero.setText("N.º " + paciente.getNumExpediente());
        lblPaciente.setText(paciente.getNombre());
        lblEspecie.setText(paciente.getEspecieTexto());
        lblRaza.setText(texto(paciente.getRaza()));
        lblSexo.setText(paciente.getSexoTexto());
        lblEdad.setText(paciente.getEdadTexto());
        if (paciente.getOwner() != null) {
            lblPropietario.setText(texto(paciente.getOwner().getNombreOwner()));
            lblTelefono.setText(texto(paciente.getOwner().getTelefono()));
        }
        txtVacunas.setText(paciente.getHistorialVacunas());
        txtDesparasitacion.setText(paciente.getHistorialDesparasitacion());
        txtParasitos.setText(paciente.getControlParasitos());
        txtObservaciones.setText(paciente.getObservaciones());
    }

    @FXML
    private void guardar(ActionEvent event) {
        paciente.setHistorialVacunas(txtVacunas.getText());
        paciente.setHistorialDesparasitacion(txtDesparasitacion.getText());
        paciente.setControlParasitos(txtParasitos.getText());
        paciente.setObservaciones(txtObservaciones.getText());
        try {
            new PacienteRepository(JpaUtil.getEntityManagerFactory()).actualizarExpediente(paciente, Sesion.getUsuario().getId());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Navegacion.alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar el expediente: " + ex.getMessage());
            return;
        }
        Navegacion.alerta(Alert.AlertType.INFORMATION, "Expediente guardado",
                "Los cambios del expediente se guardaron correctamente.");
    }

    @FXML
    private void volver(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }

    private static String texto(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
