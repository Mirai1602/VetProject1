package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Consulta;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.repositories.ConsultaRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.ReporteService;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.File;
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
    private Label lblDireccion;
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
    private Button btnEliminarConsulta;
    @FXML
    private Button btnExportarPdf;
    @FXML
    private Button btnExportarExcel;

    private Paciente paciente;
    /** true mientras se genera un reporte, para no lanzar dos exportaciones a la vez. */
    private final BooleanProperty exportando = new SimpleBooleanProperty(false);

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
        BooleanBinding sinSeleccion = tablaConsultas.getSelectionModel().selectedItemProperty().isNull();
        btnVerConsulta.disableProperty().bind(sinSeleccion);
        btnEliminarConsulta.disableProperty().bind(sinSeleccion);
        // Exportar solo se activa con una consulta seleccionada (y no mientras se genera otro reporte)
        btnExportarPdf.disableProperty().bind(sinSeleccion.or(exportando));
        btnExportarExcel.disableProperty().bind(sinSeleccion.or(exportando));
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
        mostrarPropietario();
        cargarConsultas();
    }

    private void mostrarPropietario() {
        if (paciente.getOwner() != null) {
            lblPropietario.setText(texto(paciente.getOwner().getNombreOwner()));
            lblDireccion.setText(texto(paciente.getOwner().getDireccion()));
            lblTelefono.setText(texto(paciente.getOwner().getTelefono()));
        }
    }

    @FXML
    private void editarPropietario(ActionEvent event) {
        if (Navegacion.abrirEditarPropietario((Node) event.getSource(), paciente)) {
            mostrarPropietario();
        }
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
    private void exportarPdf(ActionEvent event) {
        exportar(event, btnExportarPdf, "PDF", "pdf", ReporteService::exportarHistorialPdf);
    }

    @FXML
    private void exportarExcel(ActionEvent event) {
        exportar(event, btnExportarExcel, "Excel", "xlsx", ReporteService::exportarHistorialExcel);
    }

    /** Forma de exportar el historial (PDF, Excel…) con JasperReports. */
    @FunctionalInterface
    private interface Exportador {
        void exportar(String negocio, Paciente paciente, List<Consulta> consultas, File destino) throws Exception;
    }

    /**
     * Exporta la consulta seleccionada en la tabla. Pide dónde guardar y genera el reporte
     * en segundo plano para no congelar la ventana.
     */
    private void exportar(ActionEvent event, Button boton, String formato, String extension, Exportador exportador) {
        Consulta seleccionada = tablaConsultas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            return;
        }
        String fecha = seleccionada.getFecha() == null ? "sin_fecha" : seleccionada.getFecha().toString();
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar consulta en " + formato);
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato, "*." + extension));
        selector.setInitialFileName("Consulta_" + paciente.getNombre().replaceAll("[^\\p{L}\\p{N}]+", "_")
                + "_" + fecha + "." + extension);
        File destino = selector.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (destino == null) {
            return;
        }
        if (!destino.getName().toLowerCase().endsWith("." + extension)) {
            destino = new File(destino.getParentFile(), destino.getName() + "." + extension);
        }

        File archivo = destino;
        List<Consulta> consultas = List.of(seleccionada);
        String negocio = Sesion.getUsuario().getNombreNegocio();
        Task<Void> tarea = new Task<>() {
            @Override
            protected Void call() throws Exception {
                exportador.exportar(negocio, paciente, consultas, archivo);
                return null;
            }
        };
        String textoOriginal = boton.getText();
        exportando.set(true);
        boton.setText("Generando…");
        tarea.setOnSucceeded(e -> {
            restaurarBotones(boton, textoOriginal);
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "Se guardó la consulta en:\n" + archivo.getAbsolutePath());
            ok.setHeaderText(formato + " generado");
            ok.showAndWait();
        });
        tarea.setOnFailed(e -> {
            restaurarBotones(boton, textoOriginal);
            tarea.getException().printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR,
                    "No se pudo generar el " + formato + ": " + tarea.getException().getMessage());
            error.setHeaderText("Error al exportar");
            error.showAndWait();
        });
        Thread hilo = new Thread(tarea, "exportar-historial-" + extension);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void restaurarBotones(Button boton, String texto) {
        boton.setText(texto);
        exportando.set(false);
    }

    @FXML
    private void verConsulta(ActionEvent event) {
        Consulta seleccionada = tablaConsultas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            abrir((Node) event.getSource(), seleccionada);
        }
    }

    @FXML
    private void eliminarConsulta(ActionEvent event) {
        Consulta seleccionada = tablaConsultas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            return;
        }
        String fecha = seleccionada.getFecha() == null ? "" : " del " + seleccionada.getFecha().format(FECHA);
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminará la consulta" + fecha + " y sus vacunas aplicadas.\nEsta acción no se puede deshacer.",
                ButtonType.YES, ButtonType.NO);
        confirmar.setTitle("Eliminar consulta");
        confirmar.setHeaderText("¿Eliminar esta consulta?");
        if (confirmar.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) {
            return;
        }
        try {
            new ConsultaRepository(JpaUtil.getEntityManagerFactory())
                    .eliminar(seleccionada.getId(), Sesion.getUsuario().getId());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Navegacion.alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo eliminar la consulta: " + ex.getMessage());
            return;
        }
        cargarConsultas();
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
