package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Consulta;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.repositories.ConsultaRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/** Expediente del paciente: sus datos permanentes y el listado de todas sus consultas. */
public class HistorialConsultasController {

    static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
    private Label lblTotal;
    @FXML
    private TableView<Consulta> tablaConsultas;
    @FXML
    private TableColumn<Consulta, String> colFecha;
    @FXML
    private TableColumn<Consulta, String> colMotivo;
    @FXML
    private TableColumn<Consulta, String> colTemperatura;
    @FXML
    private TableColumn<Consulta, String> colPeso;
    @FXML
    private Button btnVerConsulta;

    private Paciente paciente;

    @FXML
    private void initialize() {
        columna(colFecha, c -> c.getFecha() == null ? "" : c.getFecha().format(FECHA));
        columna(colMotivo, c -> valor(c.getMotivoConsulta()));
        columna(colTemperatura, c -> c.getTemperatura() == null ? "—" : numero(c.getTemperatura()) + " °C");
        columna(colPeso, c -> c.getPeso() == null ? "—" : numero(c.getPeso()) + " kg");

        tablaConsultas.setPlaceholder(new Label("Este paciente todavía no tiene consultas registradas."));
        tablaConsultas.setRowFactory(tv -> {
            TableRow<Consulta> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) {
                    abrir(fila, fila.getItem());
                }
            });
            return fila;
        });
        btnVerConsulta.disableProperty().bind(
                tablaConsultas.getSelectionModel().selectedItemProperty().isNull());
    }

    void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        lblTitulo.setText("Expediente de " + paciente.getNombre());
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
        cargarConsultas();
    }

    private void cargarConsultas() {
        try {
            tablaConsultas.getItems().setAll(new ConsultaRepository(JpaUtil.getEntityManagerFactory())
                    .listarPorPaciente(paciente.getId(), Sesion.getUsuario().getId()));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            tablaConsultas.setPlaceholder(new Label("No se pudieron cargar las consultas."));
        }
        int total = tablaConsultas.getItems().size();
        lblTotal.setText(total == 1 ? "1 consulta" : total + " consultas");
    }

    @FXML
    private void nuevaConsulta(ActionEvent event) {
        Navegacion.abrirNuevaConsulta((Node) event.getSource(), paciente);
        cargarConsultas();
    }

    @FXML
    private void verConsulta(ActionEvent event) {
        Consulta seleccionada = tablaConsultas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            abrir((Node) event.getSource(), seleccionada);
        }
    }

    private void abrir(Node origen, Consulta consulta) {
        Navegacion.abrirConsulta(origen, paciente, consulta.getId());
    }

    @FXML
    private void volver(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }

    private static void columna(TableColumn<Consulta, String> col, Function<Consulta, String> valor) {
        col.setCellValueFactory(c -> new SimpleStringProperty(valor.apply(c.getValue())));
    }

    /** 38.0 → "38"; 38.5 → "38.5". */
    static String numero(Double n) {
        return n % 1 == 0 ? String.valueOf(n.longValue()) : String.valueOf(n);
    }

    private static String valor(String s) {
        return s == null ? "" : s.replace('\n', ' ');
    }

    private static String texto(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
