package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Consulta;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.repositories.ConsultaRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.ReporteService;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    @FXML
    private Button btnExportarPdf;

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

    /** Genera el PDF del historial en segundo plano para no congelar la ventana. */
    @FXML
    private void exportarPdf(ActionEvent event) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar historial en PDF");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        selector.setInitialFileName("Historial_" + paciente.getNombre().replaceAll("[^\\p{L}\\p{N}]+", "_")
                + "_" + LocalDate.now() + ".pdf");
        File destino = selector.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (destino == null) {
            return;
        }
        if (!destino.getName().toLowerCase().endsWith(".pdf")) {
            destino = new File(destino.getParentFile(), destino.getName() + ".pdf");
        }

        File archivo = destino;
        List<Consulta> consultas = List.copyOf(tablaConsultas.getItems());
        String negocio = Sesion.getUsuario().getNombreNegocio();
        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ReporteService.exportarHistorialPdf(negocio, paciente, consultas, archivo);
                return null;
            }
        };
        btnExportarPdf.setDisable(true);
        btnExportarPdf.setText("Generando…");
        tarea.setOnSucceeded(e -> {
            restaurarBotonExportar();
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Se guardó el historial en:\n" + archivo.getAbsolutePath());
            ok.setHeaderText("PDF generado");
            ok.showAndWait();
        });
        tarea.setOnFailed(e -> {
            restaurarBotonExportar();
            tarea.getException().printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR, "No se pudo generar el PDF: " + tarea.getException().getMessage());
            error.setHeaderText("Error al exportar");
            error.showAndWait();
        });
        Thread hilo = new Thread(tarea, "exportar-historial-pdf");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void restaurarBotonExportar() {
        btnExportarPdf.setDisable(false);
        btnExportarPdf.setText("📄 Exportar PDF");
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
