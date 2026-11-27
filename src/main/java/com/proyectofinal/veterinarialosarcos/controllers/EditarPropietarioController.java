package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.Propietario;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/** Ventana para modificar los datos del propietario de un paciente. */
public class EditarPropietarioController {

    @FXML
    private Label lblPaciente;
    @FXML
    private TextField txtNombreOwner;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<Telefono.Pais> cmbCodigoTelefono;
    @FXML
    private TextField txtTelefono;

    private Paciente paciente;
    private boolean guardado;

    @FXML
    private void initialize() {
        Telefono.configurar(cmbCodigoTelefono, txtTelefono);
    }

    void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        lblPaciente.setText("Propietario de " + paciente.getNombre());
        Propietario owner = paciente.getOwner();
        if (owner != null) {
            txtNombreOwner.setText(owner.getNombreOwner());
            txtDireccion.setText(owner.getDireccion());
            Telefono.mostrar(cmbCodigoTelefono, txtTelefono, owner.getTelefono());
        }
    }

    /** true si el usuario guardó los cambios (para refrescar la ventana que la abrió). */
    boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void guardar(ActionEvent event) {
        if (Validacion.estaVacio(txtNombreOwner)) {
            Navegacion.alerta(Alert.AlertType.WARNING, "Datos incompletos", "Ingresa el nombre del propietario.");
            return;
        }
        String error = Telefono.validar(cmbCodigoTelefono, txtTelefono);
        if (error != null) {
            Navegacion.alerta(Alert.AlertType.WARNING, "Teléfono", error);
            return;
        }

        String nombre = txtNombreOwner.getText().trim();
        String direccion = txtDireccion.getText() == null ? "" : txtDireccion.getText().trim();
        String telefono = Telefono.valor(cmbCodigoTelefono, txtTelefono);
        try {
            new PacienteRepository(JpaUtil.getEntityManagerFactory()).actualizarPropietario(
                    paciente.getId(), nombre, direccion, telefono, Sesion.getUsuario().getId());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Navegacion.alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron guardar los cambios: " + ex.getMessage());
            return;
        }

        // Refleja el cambio en el objeto que ya muestran las demás ventanas.
        Propietario owner = paciente.getOwner();
        if (owner != null) {
            owner.setNombreOwner(nombre);
            owner.setDireccion(direccion);
            owner.setTelefono(telefono);
        }
        guardado = true;
        Navegacion.alerta(Alert.AlertType.INFORMATION, "Propietario actualizado",
                "Los datos del propietario se guardaron correctamente.");
        Navegacion.cerrarVentana(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }
}
