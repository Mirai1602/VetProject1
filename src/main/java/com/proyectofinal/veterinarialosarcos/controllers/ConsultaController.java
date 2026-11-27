package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.enumerados.EstadoRevision;
import com.proyectofinal.veterinarialosarcos.models.Consulta;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.models.Vacuna;
import com.proyectofinal.veterinarialosarcos.models.VacunaAplicada;
import com.proyectofinal.veterinarialosarcos.repositories.ConsultaRepository;
import com.proyectofinal.veterinarialosarcos.repositories.VacunaRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Ficha de una consulta (una hoja de la ficha clínica física).
 * Se abre en blanco para registrar una visita nueva, o en solo lectura para ver una anterior.
 */
public class ConsultaController {

    /** Apartado del examen clínico: nombre en la hoja + cómo leerlo/escribirlo en la consulta. */
    private record Apartado(String nombre,
                            Function<Consulta, EstadoRevision> leer,
                            BiConsumer<Consulta, EstadoRevision> escribir) {
    }

    private static final List<Apartado> EXAMEN = List.of(
            new Apartado("Aspecto general", Consulta::getAspectoGeneral, Consulta::setAspectoGeneral),
            new Apartado("Piel y mucosas", Consulta::getPielMucosas, Consulta::setPielMucosas),
            new Apartado("Respiratorio", Consulta::getRespiratorio, Consulta::setRespiratorio),
            new Apartado("Sistema urinario", Consulta::getSistemaUrinario, Consulta::setSistemaUrinario),
            new Apartado("Circulatorio", Consulta::getCirculatorio, Consulta::setCirculatorio),
            new Apartado("Digestivo", Consulta::getDigestivo, Consulta::setDigestivo),
            new Apartado("Ojos", Consulta::getOjos, Consulta::setOjos),
            new Apartado("Oídos", Consulta::getOidos, Consulta::setOidos),
            new Apartado("Sistema nervioso", Consulta::getSistemaNervioso, Consulta::setSistemaNervioso),
            new Apartado("Ganglios linfáticos", Consulta::getGangliosLinfaticos, Consulta::setGangliosLinfaticos));

    @FXML
    private VBox formulario;
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
    private DatePicker dpFecha;
    @FXML
    private TextArea txtMotivo;
    @FXML
    private GridPane gridExamen;
    @FXML
    private TextField txtTemperatura;
    @FXML
    private TextField txtPeso;
    @FXML
    private TextArea txtDieta;
    @FXML
    private TextArea txtComentarios;
    @FXML
    private HBox boxAgregarVacuna;
    @FXML
    private ComboBox<Vacuna> cmbVacuna;
    @FXML
    private DatePicker dpFechaVacuna;
    @FXML
    private TableView<VacunaAplicada> tablaVacunas;
    @FXML
    private TableColumn<VacunaAplicada, String> colVacuna;
    @FXML
    private TableColumn<VacunaAplicada, String> colFechaVacuna;
    @FXML
    private Button btnQuitarVacuna;
    @FXML
    private Button btnGuardar;

    /** Un grupo de opciones (Normal / Anormal / No examinado) por apartado del examen. */
    private final List<ToggleGroup> gruposExamen = new ArrayList<>();

    private Paciente paciente;

    @FXML
    private void initialize() {
        construirExamen();

        // Temperatura y peso: solo números con decimales (se acepta punto o coma).
        txtTemperatura.setTextFormatter(decimal());
        txtPeso.setTextFormatter(decimal());

        dpFecha.setValue(LocalDate.now());
        dpFechaVacuna.setValue(LocalDate.now());
        // Al cambiar la fecha de la consulta, la fecha de la vacuna la sigue por defecto.
        dpFecha.valueProperty().addListener((obs, anterior, nueva) -> {
            if (dpFechaVacuna.getValue() == null || dpFechaVacuna.getValue().equals(anterior)) {
                dpFechaVacuna.setValue(nueva);
            }
        });

        colVacuna.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getVacuna().getNombre()));
        colFechaVacuna.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFechaAplicacion().format(HistorialConsultasController.FECHA)));
        tablaVacunas.setPlaceholder(new Label("Sin vacunas aplicadas en esta consulta."));
        btnQuitarVacuna.disableProperty().bind(
                tablaVacunas.getSelectionModel().selectedItemProperty().isNull());
    }

    /** Crea una fila por apartado del examen con sus tres opciones. */
    private void construirExamen() {
        int fila = 1; // la fila 0 es el encabezado definido en el FXML
        for (Apartado apartado : EXAMEN) {
            ToggleGroup grupo = new ToggleGroup();
            Label nombre = new Label(apartado.nombre());
            nombre.getStyleClass().add("examen-nombre");
            gridExamen.add(nombre, 0, fila);
            int columna = 1;
            for (EstadoRevision estado : EstadoRevision.values()) {
                RadioButton opcion = new RadioButton();
                opcion.setUserData(estado);
                opcion.setToggleGroup(grupo);
                opcion.setTooltip(new Tooltip(estado.getTexto()));
                if (estado == EstadoRevision.NO_EXAMINADO) {
                    opcion.setSelected(true);
                }
                gridExamen.add(opcion, columna++, fila);
            }
            gruposExamen.add(grupo);
            fila++;
        }
    }

    /** Prepara la ficha en blanco para una visita nueva. */
    void nuevaConsulta(Paciente paciente) {
        mostrarPaciente(paciente);
        lblTitulo.setText("Nueva consulta");
        try {
            cmbVacuna.getItems().setAll(new VacunaRepository(JpaUtil.getEntityManagerFactory())
                    .listarPorEspecie(paciente.getEspecie()));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Navegacion.alerta(Alert.AlertType.WARNING, "Vacunas",
                    "No se pudo cargar el catálogo de vacunas: " + ex.getMessage());
        }
    }

    /** Muestra una consulta ya registrada, sin permitir cambios. */
    void verConsulta(Paciente paciente, Long consultaId) {
        mostrarPaciente(paciente);
        Consulta consulta = cargar(consultaId);
        if (consulta == null) {
            return;
        }
        lblTitulo.setText("Consulta del " + consulta.getFecha().format(HistorialConsultasController.FECHA));
        llenar(consulta);

        // Solo lectura: una consulta registrada no se modifica.
        formulario.setDisable(true);
        formulario.getStyleClass().add("solo-lectura");
        ocultar(boxAgregarVacuna);
        ocultar(btnQuitarVacuna);
        ocultar(btnGuardar);
    }

    private Consulta cargar(Long consultaId) {
        try {
            return new ConsultaRepository(JpaUtil.getEntityManagerFactory())
                    .buscar(consultaId, Sesion.getUsuario().getId());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Navegacion.alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la consulta: " + ex.getMessage());
            return null;
        }
    }

    /** Pasa los datos de la consulta a los campos de la ficha. */
    private void llenar(Consulta consulta) {
        dpFecha.setValue(consulta.getFecha());
        txtMotivo.setText(consulta.getMotivoConsulta());
        for (int i = 0; i < EXAMEN.size(); i++) {
            EstadoRevision estado = EXAMEN.get(i).leer().apply(consulta);
            for (Toggle opcion : gruposExamen.get(i).getToggles()) {
                opcion.setSelected(opcion.getUserData() == estado);
            }
        }
        txtTemperatura.setText(consulta.getTemperatura() == null ? ""
                : HistorialConsultasController.numero(consulta.getTemperatura()));
        txtPeso.setText(consulta.getPeso() == null ? ""
                : HistorialConsultasController.numero(consulta.getPeso()));
        txtDieta.setText(consulta.getDieta());
        txtComentarios.setText(consulta.getComentarios());
        tablaVacunas.getItems().setAll(consulta.getVacunas());
    }

    private void mostrarPaciente(Paciente paciente) {
        this.paciente = paciente;
        lblNumero.setText("Exp. " + paciente.getNumExpediente());
        lblPaciente.setText(paciente.getNombre());
        lblEspecie.setText(paciente.getEspecieTexto());
        lblRaza.setText(texto(paciente.getRaza()));
        lblSexo.setText(paciente.getSexoTexto());
        lblEdad.setText(paciente.getEdadTexto());
        lblPropietario.setText(paciente.getOwner() == null ? "—" : texto(paciente.getOwner().getNombreOwner()));
    }

    @FXML
    private void agregarVacuna(ActionEvent event) {
        if (cmbVacuna.getValue() == null) {
            Navegacion.alerta(Alert.AlertType.WARNING, "Vacunas", "Selecciona la vacuna aplicada.");
            return;
        }
        if (dpFechaVacuna.getValue() == null) {
            Navegacion.alerta(Alert.AlertType.WARNING, "Vacunas", "Indica la fecha de aplicación.");
            return;
        }
        tablaVacunas.getItems().add(new VacunaAplicada(cmbVacuna.getValue(), dpFechaVacuna.getValue()));
        cmbVacuna.setValue(null);
    }

    @FXML
    private void quitarVacuna(ActionEvent event) {
        VacunaAplicada seleccionada = tablaVacunas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            tablaVacunas.getItems().remove(seleccionada);
        }
    }

    @FXML
    private void guardar(ActionEvent event) {
        String error = validar();
        if (error != null) {
            Navegacion.alerta(Alert.AlertType.WARNING, "Datos incompletos", error);
            return;
        }

        Consulta consulta = new Consulta();
        consulta.setFecha(dpFecha.getValue());
        consulta.setMotivoConsulta(txtMotivo.getText().trim());
        for (int i = 0; i < EXAMEN.size(); i++) {
            Toggle elegido = gruposExamen.get(i).getSelectedToggle();
            EXAMEN.get(i).escribir().accept(consulta,
                    elegido == null ? EstadoRevision.NO_EXAMINADO : (EstadoRevision) elegido.getUserData());
        }
        consulta.setTemperatura(decimal(txtTemperatura));
        consulta.setPeso(decimal(txtPeso));
        consulta.setDieta(limpio(txtDieta.getText()));
        consulta.setComentarios(limpio(txtComentarios.getText()));
        for (VacunaAplicada aplicada : tablaVacunas.getItems()) {
            consulta.agregarVacuna(new VacunaAplicada(aplicada.getVacuna(), aplicada.getFechaAplicacion()));
        }

        try {
            new ConsultaRepository(JpaUtil.getEntityManagerFactory())
                    .registrar(paciente.getId(), consulta, Sesion.getUsuario().getId());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Navegacion.alerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar la consulta: " + ex.getMessage());
            return;
        }
        Navegacion.alerta(Alert.AlertType.INFORMATION, "Consulta guardada",
                "La consulta de " + paciente.getNombre() + " se agregó a su expediente.");
        Navegacion.cerrarVentana(event);
    }

    private String validar() {
        if (dpFecha.getValue() == null) {
            return "Indica la fecha de la consulta.";
        }
        if (dpFecha.getValue().isAfter(LocalDate.now())) {
            return "La fecha de la consulta no puede ser futura.";
        }
        if (Validacion.estaVacio(txtMotivo)) {
            return "Escribe el motivo de la consulta.";
        }
        Double temperatura = decimal(txtTemperatura);
        if (temperatura != null && (temperatura < 30 || temperatura > 45)) {
            return "La temperatura debe estar entre 30 y 45 °C.";
        }
        Double peso = decimal(txtPeso);
        if (peso != null && (peso <= 0 || peso > 150)) {
            return "El peso debe ser mayor que 0 y menor de 150 kg.";
        }
        return null;
    }

    @FXML
    private void volver(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }

    private static TextFormatter<String> decimal() {
        return new TextFormatter<>(c -> c.getControlNewText().matches("\\d{0,3}([.,]\\d{0,2})?") ? c : null);
    }

    private static Double decimal(TextField campo) {
        String texto = campo.getText() == null ? "" : campo.getText().trim().replace(',', '.');
        if (texto.isEmpty() || texto.equals(".")) {
            return null;
        }
        return Double.parseDouble(texto);
    }

    private static void ocultar(javafx.scene.Node nodo) {
        nodo.setVisible(false);
        nodo.setManaged(false);
    }

    private static String limpio(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static String texto(String s) {
        return s == null || s.isBlank() ? "—" : s;
    }
}
