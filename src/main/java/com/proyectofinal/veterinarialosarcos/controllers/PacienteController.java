package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.function.Function;

public class PacienteController {

    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<Paciente> tablaPacientes;
    @FXML
    private TableColumn<Paciente, String> colNombre;
    @FXML
    private TableColumn<Paciente, String> colEspecie;
    @FXML
    private TableColumn<Paciente, String> colRaza;
    @FXML
    private TableColumn<Paciente, String> colOwner;
    @FXML
    private TableColumn<Paciente, String> colExpediente;

    private final ObservableList<Paciente> todos = FXCollections.observableArrayList();
    private final FilteredList<Paciente> pacientes = new FilteredList<>(todos);

    @FXML
    private void initialize() {
        columna(colNombre, Paciente::getNombre);
        columna(colEspecie, Paciente::getEspecieTexto);
        columna(colRaza, Paciente::getRaza);
        columna(colOwner, p -> p.getOwner() == null ? "" : p.getOwner().getNombreOwner());
        columna(colExpediente, Paciente::getNumExpediente);

        tablaPacientes.setItems(pacientes);
        tablaPacientes.setPlaceholder(new Label("No hay pacientes registrados."));
        tablaPacientes.setRowFactory(tv -> {
            TableRow<Paciente> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) {
                    Navegacion.abrirExpediente(fila, fila.getItem());
                }
            });
            return fila;
        });

        // Búsqueda en vivo por nombre, especie, raza, propietario o expediente
        txtBuscar.textProperty().addListener((obs, a, texto) -> {
            String filtro = texto == null ? "" : texto.trim().toLowerCase();
            pacientes.setPredicate(p -> filtro.isEmpty() || String.join(" ",
                    valor(p.getNombre()), p.getEspecieTexto(), valor(p.getRaza()),
                    p.getOwner() == null ? "" : valor(p.getOwner().getNombreOwner()),
                    valor(p.getNumExpediente())).toLowerCase().contains(filtro));
        });

        cargarPacientes();
    }

    private void cargarPacientes() {
        try {
            todos.setAll(new PacienteRepository(JpaUtil.getEntityManagerFactory()).listarPorUsuario(Sesion.getUsuario().getId()));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            tablaPacientes.setPlaceholder(new Label("No se pudieron cargar los pacientes."));
        }
    }
    @FXML
    private void nuevoPaciente(ActionEvent event) {
        Navegacion.abrirModal((Node) event.getSource(), "NuevoPaciente.fxml", "Nuevo Paciente", null);
        cargarPacientes();
    }

    @FXML
    private void verExpediente(ActionEvent event) {
        Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Navegacion.alerta(Alert.AlertType.INFORMATION, "Ver expediente",
                    "Selecciona un paciente de la tabla.");
            return;
        }
        Navegacion.abrirExpediente((Node) event.getSource(), seleccionado);
    }

    @FXML
    private void editarPropietario(ActionEvent event) {
        Paciente seleccionado = tablaPacientes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Navegacion.alerta(Alert.AlertType.INFORMATION, "Editar propietario",
                    "Selecciona un paciente de la tabla.");
            return;
        }
        if (Navegacion.abrirEditarPropietario((Node) event.getSource(), seleccionado)) {
            cargarPacientes(); // el propietario puede tener otras mascotas en la tabla
        }
    }

    @FXML
    private void regresar(ActionEvent event) {
        Navegacion.irA(event, "Dashboard.fxml");
    }

    private static void columna(TableColumn<Paciente, String> col, Function<Paciente, String> valor) {
        col.setCellValueFactory(c -> new SimpleStringProperty(valor.apply(c.getValue())));
    }

    private static String valor(String s) {
        return s == null ? "" : s;
    }
}
