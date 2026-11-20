package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.HelloApplication;
import com.proyectofinal.veterinarialosarcos.models.PacienteResumen;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;
import com.proyectofinal.veterinarialosarcos.services.PacienteService;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class PacientesController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblPlaceholder;
    @FXML
    private Button btnEliminar;
    @FXML
    private TableView<PacienteResumen> tblPacientes;
    @FXML
    private TableColumn<PacienteResumen, String> colNombre;
    @FXML
    private TableColumn<PacienteResumen, String> colDueno;
    @FXML
    private TableColumn<PacienteResumen, LocalDateTime> colUltimaCita;
    @FXML
    private TableColumn<PacienteResumen, String> colExpediente;

    private final PacienteService service =
            new PacienteService(new PacienteRepository(JpaUtil.getEntityManagerFactory()));

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nombre()));
        colDueno.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dueno()));
        colExpediente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().expediente()));

        // El valor de la celda es la fecha real (para ordenar bien); solo se formatea al mostrarla
        colUltimaCita.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().ultimaCita()));
        colUltimaCita.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                if (vacia) {
                    setText(null);
                } else {
                    setText(fecha == null ? "Sin citas" : fecha.format(FORMATO_FECHA));
                }
            }
        });
        colUltimaCita.setComparator(Comparator.nullsFirst(Comparator.naturalOrder()));

        // Eliminar solo se habilita cuando hay un paciente seleccionado
        btnEliminar.disableProperty().bind(
                tblPacientes.getSelectionModel().selectedItemProperty().isNull());

        // Doble clic sobre una fila con datos = actualizar ese paciente
        tblPacientes.setRowFactory(tabla -> {
            TableRow<PacienteResumen> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !fila.isEmpty()) {
                    abrirFormulario(fila.getItem().id());
                }
            });
            return fila;
        });

        Usuario usuario = Sesion.getUsuario();
        lblBienvenida.setText("Pacientes de " + usuario.getNombreParaMostrar());

        cargarPacientes(usuario);
    }

    private void cargarPacientes(Usuario usuario) {
        try {
            List<PacienteResumen> pacientes = service.listarPacientes(usuario);
            tblPacientes.setItems(FXCollections.observableArrayList(pacientes));
            lblTotal.setText(pacientes.size() + (pacientes.size() == 1 ? " paciente" : " pacientes"));
            lblPlaceholder.setText("Aún no hay pacientes registrados.");
        } catch (RuntimeException ex) {
            tblPacientes.getItems().clear();
            lblTotal.setText("");
            lblPlaceholder.setText("No se pudieron cargar los pacientes.");
        }
    }

    @FXML
    private void agregar(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void eliminar(ActionEvent event) {
        PacienteResumen seleccionado = tblPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminará a " + seleccionado.nombre() + " (expediente " + seleccionado.expediente()
                        + ") junto con sus citas. Esta acción no se puede deshacer.",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Eliminar paciente");
        confirmacion.setHeaderText("¿Eliminar a " + seleccionado.nombre() + "?");
        confirmacion.initOwner(tblPacientes.getScene().getWindow());

        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }

        try {
            service.eliminarPaciente(seleccionado.id());
        } catch (RuntimeException ex) {
            mostrarError("No se pudo eliminar el paciente. Intenta de nuevo.");
        }
        cargarPacientes(Sesion.getUsuario());
    }

    /** id null = agregar; con id = actualizar ese paciente. Recarga la tabla si se guardó. */
    private void abrirFormulario(Long idPaciente) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("PacienteForm.fxml"));
            Parent vista = loader.load();
            PacienteFormController form = loader.getController();
            if (idPaciente == null) {
                form.nuevo(service);
            } else {
                form.editar(service, idPaciente);
            }

            Stage ventana = new Stage();
            ventana.setTitle(idPaciente == null ? "Agregar paciente" : "Actualizar paciente");
            ventana.initOwner(tblPacientes.getScene().getWindow());
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.setResizable(false);
            ventana.setScene(new Scene(vista));
            ventana.showAndWait();

            if (form.isGuardado()) {
                cargarPacientes(Sesion.getUsuario());
            }
        } catch (IOException | RuntimeException ex) {
            mostrarError("No se pudo abrir el formulario del paciente.");
        }
    }

    private void mostrarError(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, texto, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.initOwner(tblPacientes.getScene().getWindow());
        alerta.showAndWait();
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Sesion.cerrar();
        Navegacion.irA(event, "Login.fxml");
    }
}
