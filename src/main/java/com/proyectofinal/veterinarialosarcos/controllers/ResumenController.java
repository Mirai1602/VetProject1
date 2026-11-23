package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.enumerados.Especie;
import com.proyectofinal.veterinarialosarcos.models.Paciente;
import com.proyectofinal.veterinarialosarcos.repositories.PacienteRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Resumen de pacientes agrupados por especie. Cada especie es una fila de grupo
 * (con su total) que se puede expandir para ver sus pacientes.
 */
public class ResumenController {

    @FXML
    private Label lblTotal;
    @FXML
    private Label lblPerros;
    @FXML
    private Label lblGatos;
    @FXML
    private TreeTableView<Paciente> tablaResumen;
    @FXML
    private TreeTableColumn<Paciente, String> colNombre;
    @FXML
    private TreeTableColumn<Paciente, String> colRaza;
    @FXML
    private TreeTableColumn<Paciente, String> colSexo;
    @FXML
    private TreeTableColumn<Paciente, String> colEdad;
    @FXML
    private TreeTableColumn<Paciente, String> colOwner;
    @FXML
    private TreeTableColumn<Paciente, String> colExpediente;
    @FXML
    private Button btnVerExpediente;

    /** Nombre del grupo mostrado en la fila de cada especie. */
    private final Map<TreeItem<Paciente>, String> grupos = new java.util.HashMap<>();

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(
                grupos.containsKey(c.getValue()) ? grupos.get(c.getValue()) : c.getValue().getValue().getNombre()));
        columna(colRaza, Paciente::getRaza);
        columna(colSexo, Paciente::getSexoTexto);
        columna(colEdad, Paciente::getEdadTexto);
        columna(colOwner, p -> p.getOwner() == null ? "" : p.getOwner().getNombreOwner());
        columna(colExpediente, Paciente::getNumExpediente);

        // Las filas de grupo se ven resaltadas
        tablaResumen.setRowFactory(tv -> new TreeTableRow<>() {
            @Override
            protected void updateItem(Paciente item, boolean vacio) {
                super.updateItem(item, vacio);
                boolean esGrupo = !vacio && grupos.containsKey(getTreeItem());
                getStyleClass().remove("fila-grupo");
                if (esGrupo) {
                    getStyleClass().add("fila-grupo");
                }
            }
        });
        tablaResumen.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && pacienteSeleccionado() != null) {
                Navegacion.abrirExpediente(tablaResumen, pacienteSeleccionado());
            }
        });
        btnVerExpediente.disableProperty().bind(
                tablaResumen.getSelectionModel().selectedItemProperty().isNull());
        tablaResumen.setPlaceholder(new Label("No hay pacientes registrados."));

        cargar();
    }

    private void cargar() {
        List<Paciente> pacientes;
        try {
            pacientes = new PacienteRepository(JpaUtil.getEntityManagerFactory()).listarPorUsuario(Sesion.getUsuario().getId());
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            tablaResumen.setPlaceholder(new Label("No se pudieron cargar los pacientes."));
            return;
        }

        Map<Especie, TreeItem<Paciente>> porEspecie = new EnumMap<>(Especie.class);
        TreeItem<Paciente> sinEspecie = null;
        TreeItem<Paciente> raiz = new TreeItem<>();
        grupos.clear();

        for (Especie especie : Especie.values()) {
            TreeItem<Paciente> grupo = new TreeItem<>();
            grupo.setExpanded(true);
            porEspecie.put(especie, grupo);
        }
        for (Paciente p : pacientes) {
            TreeItem<Paciente> grupo = p.getEspecie() == null ? null : porEspecie.get(p.getEspecie());
            if (grupo == null) {
                if (sinEspecie == null) {
                    sinEspecie = new TreeItem<>();
                    sinEspecie.setExpanded(true);
                }
                grupo = sinEspecie;
            }
            grupo.getChildren().add(new TreeItem<>(p));
        }
        for (Map.Entry<Especie, TreeItem<Paciente>> e : porEspecie.entrySet()) {
            agregarGrupo(raiz, e.getValue(), icono(e.getKey()) + " " + texto(e.getKey()));
        }
        if (sinEspecie != null) {
            agregarGrupo(raiz, sinEspecie, "❔ Sin especie");
        }

        tablaResumen.setRoot(raiz);
        tablaResumen.setShowRoot(false);

        lblTotal.setText(String.valueOf(pacientes.size()));
        lblPerros.setText(String.valueOf(porEspecie.get(Especie.PERRO).getChildren().size()));
        lblGatos.setText(String.valueOf(porEspecie.get(Especie.GATO).getChildren().size()));
    }

    private void agregarGrupo(TreeItem<Paciente> raiz, TreeItem<Paciente> grupo, String nombre) {
        int total = grupo.getChildren().size();
        grupos.put(grupo, nombre + "  (" + total + (total == 1 ? " paciente)" : " pacientes)"));
        raiz.getChildren().add(grupo);
    }

    private Paciente pacienteSeleccionado() {
        TreeItem<Paciente> item = tablaResumen.getSelectionModel().getSelectedItem();
        return item == null || grupos.containsKey(item) ? null : item.getValue();
    }

    @FXML
    private void verExpediente(ActionEvent event) {
        Paciente paciente = pacienteSeleccionado();
        if (paciente == null) {
            Navegacion.alerta(Alert.AlertType.INFORMATION, "Ver expediente",
                    "Selecciona un paciente (no el nombre de la especie).");
            return;
        }
        Navegacion.abrirExpediente((Node) event.getSource(), paciente);
    }

    @FXML
    private void regresar(ActionEvent event) {
        Navegacion.irA(event, "Dashboard.fxml");
    }

    private void columna(TreeTableColumn<Paciente, String> col, Function<Paciente, String> valor) {
        col.setCellValueFactory(c -> new SimpleStringProperty(
                grupos.containsKey(c.getValue()) || c.getValue().getValue() == null
                        ? "" : valor.apply(c.getValue().getValue())));
    }

    private static String icono(Especie especie) {
        return switch (especie) {
            case PERRO -> "🐶";
            case GATO -> "🐱";
        };
    }

    private static String texto(Especie especie) {
        String n = especie.name().toLowerCase();
        return Character.toUpperCase(n.charAt(0)) + n.substring(1) + "s";
    }
}
