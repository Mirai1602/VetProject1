package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.services.PacienteService;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;

/**
 * Formulario para agregar un paciente nuevo o actualizar uno existente.
 * Se abre como ventana modal desde PacientesController.
 */
public class PacienteFormController {

    // El campo genero de Paciente es boolean: true = Macho, false = Hembra
    private static final String MACHO = "Macho";
    private static final String HEMBRA = "Hembra";

    @FXML
    private Label lblTitulo;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<Especie> cmbEspecie;
    @FXML
    private TextField txtRaza;
    @FXML
    private DatePicker dpFechaNac;
    @FXML
    private ComboBox<String> cmbGenero;
    @FXML
    private TextField txtExpediente;
    @FXML
    private TextField txtDueno;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Label lblMensaje;

    private PacienteService service;
    private Long idPaciente;   // null = paciente nuevo
    private boolean guardado;

    @FXML
    private void initialize() {
        cmbEspecie.getItems().setAll(Especie.values());
        cmbGenero.getItems().setAll(MACHO, HEMBRA);
    }

    /** Prepara el formulario vacío para agregar. */
    void nuevo(PacienteService service) {
        this.service = service;
        this.idPaciente = null;
        lblTitulo.setText("Agregar paciente");
    }

    /** Llena el formulario con los datos del paciente para actualizarlo. */
    void editar(PacienteService service, Long id) {
        this.service = service;
        this.idPaciente = id;
        lblTitulo.setText("Actualizar paciente");

        Paciente p = service.obtenerPaciente(id);
        txtNombre.setText(p.getNombre());
        cmbEspecie.setValue(p.getEspecie());
        txtRaza.setText(p.getRaza());
        dpFechaNac.setValue(p.getFechaNac());
        cmbGenero.setValue(p.isGenero() ? MACHO : HEMBRA);
        txtExpediente.setText(p.getNumExpediente());
        txtDueno.setText(p.getOwner().getNombreOwner());
        txtTelefono.setText(p.getOwner().getTelefono());
    }

    /** true si el usuario guardó cambios (para que la tabla se recargue). */
    boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void guardar(ActionEvent event) {
        Validacion.limpiar(lblMensaje, txtNombre, txtRaza, txtExpediente, txtDueno, txtTelefono);

        if (Validacion.estaVacio(txtNombre)) {
            Validacion.error(lblMensaje, txtNombre, "Ingresa el nombre del paciente.");
            return;
        }
        if (cmbEspecie.getValue() == null) {
            Validacion.error(lblMensaje, "Selecciona la especie.");
            return;
        }
        if (cmbGenero.getValue() == null) {
            Validacion.error(lblMensaje, "Selecciona el género.");
            return;
        }
        LocalDate fechaNac = dpFechaNac.getValue();
        if (fechaNac != null && fechaNac.isAfter(LocalDate.now())) {
            Validacion.error(lblMensaje, "La fecha de nacimiento no puede ser futura.");
            return;
        }
        if (Validacion.estaVacio(txtExpediente)) {
            Validacion.error(lblMensaje, txtExpediente, "Ingresa el número de expediente.");
            return;
        }
        if (Validacion.estaVacio(txtDueno)) {
            Validacion.error(lblMensaje, txtDueno, "Ingresa el nombre del dueño.");
            return;
        }
        if (Validacion.estaVacio(txtTelefono)) {
            Validacion.error(lblMensaje, txtTelefono, "Ingresa el teléfono del dueño.");
            return;
        }

        String expediente = txtExpediente.getText().trim();

        Paciente datos = new Paciente();
        datos.setNombre(txtNombre.getText().trim());
        datos.setEspecie(cmbEspecie.getValue());
        datos.setRaza(txtRaza.getText() == null ? null : txtRaza.getText().trim());
        datos.setFechaNac(fechaNac);
        datos.setGenero(MACHO.equals(cmbGenero.getValue()));
        datos.setNumExpediente(expediente);

        try {
            if (service.expedienteEnUso(expediente, idPaciente)) {
                Validacion.error(lblMensaje, txtExpediente, "Ya existe un paciente con ese expediente.");
                return;
            }
            service.guardarPaciente(idPaciente, datos,
                    txtDueno.getText().trim(), txtTelefono.getText().trim(), Sesion.getUsuario());
        } catch (RuntimeException ex) {
            Validacion.error(lblMensaje, "No se pudo guardar el paciente. Intenta de nuevo.");
            return;
        }

        guardado = true;
        cerrarVentana(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
