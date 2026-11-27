package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.Propietario;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class NuevoPacienteController {

    @FXML
    private TextField txtNombrePaciente;
    @FXML
    private ComboBox<Especie> cmbEspecie;
    @FXML
    private RadioButton rbMacho;
    @FXML
    private RadioButton rbHembra;
    @FXML
    private TextField txtRaza;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private CheckBox chkFechaDesconocida;
    @FXML
    private TextField txtEdadAproximada;
    @FXML
    private TextField txtNombreOwner;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<Telefono.Pais> cmbCodigoTelefono;
    @FXML
    private TextField txtTelefono;

    private final ToggleGroup sexoGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        rbMacho.setToggleGroup(sexoGroup);
        rbHembra.setToggleGroup(sexoGroup);

        // Teléfono: código de país (+505 por defecto) y solo dígitos.
        Telefono.configurar(cmbCodigoTelefono, txtTelefono);

        // Solo se permiten las especies del enum: Perro o Gato.
        cmbEspecie.getItems().setAll(Especie.values());
        cmbEspecie.setConverter(new StringConverter<>() {
            @Override
            public String toString(Especie especie) {
                if (especie == null) {
                    return "";
                }
                String nombre = especie.name().toLowerCase();
                return Character.toUpperCase(nombre.charAt(0)) + nombre.substring(1);
            }

            @Override
            public Especie fromString(String texto) {
                return null; // El ComboBox no es editable.
            }
        });

        // Solo se aceptan dígitos en la edad aproximada.
        txtEdadAproximada.setTextFormatter(new TextFormatter<String>(
                cambio -> cambio.getControlNewText().matches("\\d{0,2}") ? cambio : null));

        // No se permiten fechas de nacimiento futuras.
        dpFechaNacimiento.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                setDisable(vacio || fecha.isAfter(LocalDate.now()));
            }
        });

        chkFechaDesconocida.selectedProperty().addListener((obs, anterior, seleccionado) -> {
            dpFechaNacimiento.setDisable(seleccionado);
            txtEdadAproximada.setDisable(!seleccionado);
            if (seleccionado) {
                dpFechaNacimiento.setValue(null);
            } else {
                txtEdadAproximada.clear();
            }
        });
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
    private void guardarPaciente(ActionEvent event) {
        String error = validar();
        if (error != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos incompletos", error);
            return;
        }

        Propietario propietario = new Propietario();
        propietario.setNombreOwner(txtNombreOwner.getText().trim());
        propietario.setDireccion(txtDireccion.getText().trim());
        propietario.setTelefono(Telefono.valor(cmbCodigoTelefono, txtTelefono));

        Paciente paciente = new Paciente();
        paciente.setNombre(txtNombrePaciente.getText().trim());
        paciente.setEspecie(cmbEspecie.getValue());
        paciente.setSexo(obtenerSexo());

        paciente.setRaza(txtRaza.getText().trim());
        paciente.setFechaNacimientoConocida(!chkFechaDesconocida.isSelected());
        if (chkFechaDesconocida.isSelected()) {
            paciente.setEdad(Integer.parseInt(txtEdadAproximada.getText()));
        } else {
            paciente.setFechaNac(dpFechaNacimiento.getValue());
        }
        long marca = System.currentTimeMillis();
        paciente.setNumPaciente("PAC-" + marca);
        paciente.setNumExpediente("EXP-" + marca);
        paciente.setOwner(propietario);
        paciente.setUsuario(Sesion.getUsuario());

        try {
            new PacienteRepository(JpaUtil.getEntityManagerFactory()).guardar(paciente, propietario);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar el paciente: " + ex.getMessage());
            return;
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "Paciente guardado",
                "El paciente " + paciente.getNombre() + " se registró correctamente.");
        cerrarVentana(event);
    }

    private String validar() {
        if (Validacion.estaVacio(txtNombrePaciente)) {
            return "Ingresa el nombre del paciente.";
        }
        if (cmbEspecie.getValue() == null) {
            return "Selecciona la especie (Perro o Gato).";
        }
        if (obtenerSexo() == null) {
            return "Selecciona el sexo del paciente.";
        }
        if (chkFechaDesconocida.isSelected()) {
            if (Validacion.estaVacio(txtEdadAproximada)) {
                return "Ingresa la edad aproximada.";
            }
        } else if (dpFechaNacimiento.getValue() == null) {
            return "Selecciona la fecha de nacimiento o marca que no se conoce.";
        }
        if (Validacion.estaVacio(txtNombreOwner)) {
            return "Ingresa el nombre del propietario.";
        }
        return Telefono.validar(cmbCodigoTelefono, txtTelefono);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(texto);
        alerta.showAndWait();
    }
}
