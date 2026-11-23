package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import com.proyectofinal.veterinarialosarcos.utils.Sesion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Perfil del usuario que inició sesión: foto, nombre de usuario, datos personales
 * y acceso al cambio de contraseña.
 */
public class AjustesController {

    private static final int EDAD_MINIMA = 15;          // Debe ser MAYOR que 15 años
    private static final int MIN_USUARIO = 3;
    private static final long MAX_FOTO_BYTES = 2 * 1024 * 1024;
    private static final Pattern SOLO_LETRAS = Pattern.compile("[\\p{L} '\\-]{2,50}");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private ImageView imgPerfil;
    @FXML
    private Label lblInicial;
    @FXML
    private Label lblNombreVisible;
    @FXML
    private Label lblArroba;
    @FXML
    private Label lblCorreo;
    @FXML
    private Label lblMiembroDesde;
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private Label lblEdad;
    @FXML
    private Label lblMensaje;

    private final UsuarioRepository repository = new UsuarioRepository(JpaUtil.getEntityManagerFactory());

    @FXML
    private void initialize() {
        imgPerfil.setClip(new Circle(60, 60, 60));

        // Solo se pueden elegir fechas de personas mayores de 15 años
        LocalDate fechaMaxima = LocalDate.now().minusYears(EDAD_MINIMA + 1);
        dpFechaNacimiento.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacio) {
                super.updateItem(fecha, vacio);
                setDisable(vacio || fecha.isAfter(fechaMaxima));
            }
        });
        dpFechaNacimiento.valueProperty().addListener((obs, a, fecha) -> mostrarEdad(fecha));

        mostrarUsuario(Sesion.getUsuario());
    }

    private void mostrarUsuario(Usuario u) {
        if (u == null) {
            return;
        }
        lblNombreVisible.setText(u.getNombreVisible());
        lblArroba.setText("@" + u.getNombreNegocio());
        lblCorreo.setText("✉ " + u.getEmail());
        lblMiembroDesde.setText(u.getFechaCreacion() == null ? ""
                : "📅 Miembro desde " + u.getFechaCreacion().format(FORMATO));
        txtUsuario.setText(u.getNombreNegocio());
        txtNombre.setText(u.getNombre());
        txtApellido.setText(u.getApellido());
        dpFechaNacimiento.setValue(u.getFechaNacimiento());
        mostrarEdad(u.getFechaNacimiento());
        mostrarFoto(u.getFoto());
    }

    private void mostrarFoto(byte[] bytes) {
        Image foto = Navegacion.imagen(bytes);
        imgPerfil.setImage(foto);
        lblInicial.setVisible(foto == null);
        Usuario u = Sesion.getUsuario();
        if (u != null) {
            lblInicial.setText(u.getNombreVisible().substring(0, 1).toUpperCase());
        }
    }

    private void mostrarEdad(LocalDate fecha) {
        lblEdad.setText(fecha == null ? "Edad: —"
                : "Edad: " + Period.between(fecha, LocalDate.now()).getYears() + " años");
    }

    @FXML
    private void cambiarFoto(ActionEvent event) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Selecciona tu foto de perfil");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File archivo = selector.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (archivo == null) {
            return;
        }
        Validacion.limpiar(lblMensaje);
        if (archivo.length() > MAX_FOTO_BYTES) {
            Validacion.error(lblMensaje, null, "La imagen debe pesar 2 MB o menos.");
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(archivo.toPath());
            if (Navegacion.imagen(bytes).isError()) {
                Validacion.error(lblMensaje, null, "El archivo no es una imagen válida.");
                return;
            }
            Usuario u = Sesion.getUsuario();
            u.setFoto(bytes);
            Sesion.iniciar(repository.actualizar(u));
            mostrarFoto(bytes);
            Validacion.exito(lblMensaje, "¡Foto de perfil actualizada!");
        } catch (IOException | RuntimeException ex) {
            ex.printStackTrace();
            Validacion.error(lblMensaje, null, "No se pudo guardar la foto.");
        }
    }

    @FXML
    private void quitarFoto() {
        Validacion.limpiar(lblMensaje);
        try {
            Usuario u = Sesion.getUsuario();
            u.setFoto(null);
            Sesion.iniciar(repository.actualizar(u));
            mostrarFoto(null);
            Validacion.exito(lblMensaje, "Se quitó la foto de perfil.");
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Validacion.error(lblMensaje, null, "No se pudo quitar la foto.");
        }
    }

    @FXML
    private void guardarCambios() {
        Validacion.limpiar(lblMensaje, txtUsuario, txtNombre, txtApellido);
        dpFechaNacimiento.getStyleClass().remove("input-error");
        Usuario u = Sesion.getUsuario();

        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        if (usuario.length() < MIN_USUARIO) {
            Validacion.error(lblMensaje, txtUsuario,
                    "El nombre de usuario debe tener al menos " + MIN_USUARIO + " caracteres.");
            return;
        }
        if (usuario.contains(" ")) {
            Validacion.error(lblMensaje, txtUsuario, "El nombre de usuario no puede tener espacios.");
            return;
        }
        String nombre = limpio(txtNombre);
        if (nombre != null && !SOLO_LETRAS.matcher(nombre).matches()) {
            Validacion.error(lblMensaje, txtNombre, "El nombre solo puede contener letras.");
            return;
        }
        String apellido = limpio(txtApellido);
        if (apellido != null && !SOLO_LETRAS.matcher(apellido).matches()) {
            Validacion.error(lblMensaje, txtApellido, "El apellido solo puede contener letras.");
            return;
        }
        LocalDate fecha = dpFechaNacimiento.getValue();
        if (fecha != null) {
            if (fecha.isAfter(LocalDate.now())) {
                errorFecha("La fecha de nacimiento no puede ser futura.");
                return;
            }
            if (Period.between(fecha, LocalDate.now()).getYears() <= EDAD_MINIMA) {
                errorFecha("Debes ser mayor de " + EDAD_MINIMA + " años.");
                return;
            }
            if (fecha.isBefore(LocalDate.now().minusYears(120))) {
                errorFecha("Revisa la fecha de nacimiento.");
                return;
            }
        }

        try {
            if (!usuario.equals(u.getNombreNegocio())) {
                Usuario existente = repository.buscarPorNombreUsuario(usuario);
                if (existente != null && existente.getId() != u.getId()) {
                    Validacion.error(lblMensaje, txtUsuario, "Ese nombre de usuario ya está en uso.");
                    return;
                }
            }
            u.setNombreNegocio(usuario);
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setFechaNacimiento(fecha);
            Sesion.iniciar(repository.actualizar(u));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            Validacion.error(lblMensaje, null, "No se pudieron guardar los cambios.");
            return;
        }
        mostrarUsuario(Sesion.getUsuario());
        Validacion.exito(lblMensaje, "¡Perfil actualizado!");
    }

    private void errorFecha(String texto) {
        if (!dpFechaNacimiento.getStyleClass().contains("input-error")) {
            dpFechaNacimiento.getStyleClass().add("input-error");
        }
        Validacion.error(lblMensaje, null, texto);
    }

    @FXML
    private void cambiarContrasena(ActionEvent event) {
        Validacion.limpiar(lblMensaje);
        Navegacion.abrirModal((Node) event.getSource(), "CambiarContrasena.fxml",
                "Cambiar contraseña", null);
    }

    @FXML
    private void regresar(ActionEvent event) {
        Navegacion.irA(event, "Dashboard.fxml");
    }

    private static String limpio(TextField campo) {
        String t = campo.getText() == null ? "" : campo.getText().trim();
        return t.isEmpty() ? null : t;
    }
}
