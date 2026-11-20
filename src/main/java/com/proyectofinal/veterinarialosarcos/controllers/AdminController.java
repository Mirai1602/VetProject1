package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.HelloApplication;
import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;
import com.proyectofinal.veterinarialosarcos.models.VeterinarioResumen;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.services.UsuarioService;
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
import java.util.List;

/**
 * Vista del administrador: tabla de veterinarios con sus datos y pacientes, y alta de nuevos veterinarios.
 */
public class AdminController {

    @FXML
    private Label lblTotal;
    @FXML
    private Label lblAdmin;
    @FXML
    private Label lblPlaceholder;
    @FXML
    private TableView<VeterinarioResumen> tblVeterinarios;
    @FXML
    private TableColumn<VeterinarioResumen, String> colNombres;
    @FXML
    private TableColumn<VeterinarioResumen, String> colApellidos;
    @FXML
    private TableColumn<VeterinarioResumen, Sexo> colSexo;
    @FXML
    private TableColumn<VeterinarioResumen, Integer> colEdad;
    @FXML
    private TableColumn<VeterinarioResumen, String> colEmail;
    @FXML
    private TableColumn<VeterinarioResumen, String> colTelefono;
    @FXML
    private TableColumn<VeterinarioResumen, String> colUsuario;
    @FXML
    private TableColumn<VeterinarioResumen, String> colPassword;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TableColumn<VeterinarioResumen, String> colCedula;
    @FXML
    private TableColumn<VeterinarioResumen, Integer> colConsultorio;
    @FXML
    private TableColumn<VeterinarioResumen, String> colEspecialidad;
    @FXML
    private TableColumn<VeterinarioResumen, Long> colAtendidos;
    @FXML
    private TableColumn<VeterinarioResumen, Long> colAtendiendo;

    private final UsuarioService service =
            new UsuarioService(new UsuarioRepository(JpaUtil.getEntityManagerFactory()));

    @FXML
    private void initialize() {
        colNombres.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nombres()));
        colApellidos.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().apellidos()));
        colSexo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().sexo()));
        colEdad.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().edad()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().email()));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().telefono()));
        colCedula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().cedula()));
        colConsultorio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().numConsultorio()));
        colEspecialidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().especialidad()));
        colAtendidos.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().pacientesAtendidos()));
        colAtendiendo.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().pacientesAtendiendo()));

        // Datos que pueden faltar en cuentas creadas antes de este cambio se muestran como "—"
        colNombres.setCellFactory(columna -> celdaConGuion());
        colApellidos.setCellFactory(columna -> celdaConGuion());
        colSexo.setCellFactory(columna -> celdaConGuion());
        colEdad.setCellFactory(columna -> celdaConGuion());
        colTelefono.setCellFactory(columna -> celdaConGuion());
        colCedula.setCellFactory(columna -> celdaConGuion());
        colConsultorio.setCellFactory(columna -> celdaConGuion());
        colEspecialidad.setCellFactory(columna -> celdaConGuion());

        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().usuario()));
        colPassword.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().password()));

        // Editar y Eliminar solo se habilitan con un veterinario seleccionado
        var sinSeleccion = tblVeterinarios.getSelectionModel().selectedItemProperty().isNull();
        btnEditar.disableProperty().bind(sinSeleccion);
        btnEliminar.disableProperty().bind(sinSeleccion);

        // Doble clic sobre una fila = editar ese veterinario
        tblVeterinarios.setRowFactory(tabla -> {
            TableRow<VeterinarioResumen> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !fila.isEmpty()) {
                    abrirFormulario(fila.getItem().id());
                }
            });
            return fila;
        });

        lblAdmin.setText("Admin: " + Sesion.getUsuario().getNombreParaMostrar());

        cargarVeterinarios();
    }

    private static <T> TableCell<VeterinarioResumen, T> celdaConGuion() {
        return new TableCell<>() {
            @Override
            protected void updateItem(T valor, boolean vacia) {
                super.updateItem(valor, vacia);
                setText(vacia ? null : (valor == null ? "—" : valor.toString()));
            }
        };
    }

    private void cargarVeterinarios() {
        try {
            List<VeterinarioResumen> veterinarios = service.listarVeterinarios();
            tblVeterinarios.setItems(FXCollections.observableArrayList(veterinarios));
            lblTotal.setText(veterinarios.size()
                    + (veterinarios.size() == 1 ? " veterinario registrado" : " veterinarios registrados"));
            lblPlaceholder.setText("Aún no hay veterinarios registrados.");
        } catch (RuntimeException ex) {
            tblVeterinarios.getItems().clear();
            lblTotal.setText("");
            lblPlaceholder.setText("No se pudieron cargar los veterinarios.");
        }
    }

    @FXML
    private void agregar(ActionEvent event) {
        abrirFormulario(null);
    }

    @FXML
    private void editar(ActionEvent event) {
        VeterinarioResumen seleccionado = tblVeterinarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormulario(seleccionado.id());
        }
    }

    @FXML
    private void eliminar(ActionEvent event) {
        VeterinarioResumen seleccionado = tblVeterinarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }
        String nombre = seleccionado.nombres() != null
                ? seleccionado.nombres() + " " + seleccionado.apellidos()
                : seleccionado.usuario();

        try {
            // Sus pacientes quedarían sin veterinario y desaparecerían de todas las vistas
            long pacientes = service.pacientesAsignados(seleccionado.id());
            if (pacientes > 0) {
                mostrarError("No se puede eliminar a " + nombre + " porque tiene " + pacientes
                        + (pacientes == 1 ? " paciente asignado." : " pacientes asignados."));
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                    "Se eliminará la cuenta de " + nombre + " (usuario " + seleccionado.usuario()
                            + "). Ya no podrá iniciar sesión. Esta acción no se puede deshacer.",
                    ButtonType.YES, ButtonType.NO);
            confirmacion.setTitle("Eliminar veterinario");
            confirmacion.setHeaderText("¿Eliminar a " + nombre + "?");
            confirmacion.initOwner(tblVeterinarios.getScene().getWindow());
            if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
                return;
            }

            service.eliminarVeterinario(seleccionado.id());
        } catch (RuntimeException ex) {
            mostrarError("No se pudo eliminar al veterinario. Intenta de nuevo.");
        }
        cargarVeterinarios();
    }

    /** id null = agregar; con id = editar ese veterinario. Recarga la tabla si se guardó. */
    private void abrirFormulario(Integer idVeterinario) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("VeterinarioForm.fxml"));
            Parent vista = loader.load();
            VeterinarioFormController form = loader.getController();
            if (idVeterinario != null) {
                form.editar(idVeterinario);
            }

            Stage ventana = new Stage();
            ventana.setTitle(idVeterinario == null ? "Nuevo veterinario" : "Editar veterinario");
            ventana.initOwner(tblVeterinarios.getScene().getWindow());
            ventana.initModality(Modality.WINDOW_MODAL);
            ventana.setResizable(false);
            ventana.setScene(new Scene(vista));
            ventana.showAndWait();

            if (form.isGuardado()) {
                cargarVeterinarios();
            }
        } catch (IOException | RuntimeException ex) {
            mostrarError("No se pudo abrir el formulario de veterinario.");
        }
    }

    private void mostrarError(String texto) {
        Alert alerta = new Alert(Alert.AlertType.ERROR, texto, ButtonType.OK);
        alerta.setHeaderText(null);
        alerta.initOwner(tblVeterinarios.getScene().getWindow());
        alerta.showAndWait();
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Sesion.cerrar();
        Navegacion.irA(event, "Login.fxml");
    }
}
