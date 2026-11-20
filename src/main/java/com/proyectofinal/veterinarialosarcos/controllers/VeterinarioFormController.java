package com.proyectofinal.veterinarialosarcos.controllers;

import com.proyectofinal.veterinarialosarcos.enumerados.Sexo;
import com.proyectofinal.veterinarialosarcos.models.Usuario;
import com.proyectofinal.veterinarialosarcos.repositories.UsuarioRepository;
import com.proyectofinal.veterinarialosarcos.services.UsuarioService;
import com.proyectofinal.veterinarialosarcos.utils.JpaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Formulario (antes la pantalla de Registro) con el que el administrador da de alta veterinarios.
 * Se abre como ventana modal desde AdminController.
 */
public class VeterinarioFormController {

    private static final int MIN_USUARIO = 3;
    private static final int MIN_PASSWORD = 6;
    private static final int EDAD_MINIMA = 18;
    private static final int LARGO_CEDULA = 16;
    private static final int MAX_DIGITOS_CONSULTORIO = 4;
    // Formato de cédula: 3 dígitos - 6 dígitos - 4 dígitos + 1 letra (16 caracteres con guiones)
    private static final Pattern CEDULA = Pattern.compile("\\d{3}-\\d{6}-\\d{4}[A-Z]");
    private static final String EJEMPLO_CEDULA = "001-220807-1019M";

    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private ComboBox<Sexo> cmbSexo;
    @FXML
    private DatePicker dpFechaNac;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCedula;
    @FXML
    private TextField txtConsultorio;
    @FXML
    private TextField txtEspecialidad;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Label lblMensaje;

    private final UsuarioService service =
            new UsuarioService(new UsuarioRepository(JpaUtil.getEntityManagerFactory()));

    @FXML
    private Label lblTitulo;
    @FXML
    private Button btnGuardar;

    private Integer idVeterinario;   // null = veterinario nuevo
    private boolean guardado;

    @FXML
    private void initialize() {
        cmbSexo.getItems().setAll(Sexo.values());

        // Cédula: solo letras, números y guiones, hasta 16 caracteres (la letra se guarda en mayúscula)
        txtCedula.setTextFormatter(new TextFormatter<>(cambio -> {
            cambio.setText(cambio.getText().toUpperCase());
            String nuevo = cambio.getControlNewText();
            return nuevo.matches("[A-Z0-9-]{0," + LARGO_CEDULA + "}") ? cambio : null;
        }));
        // Consultorio: solo números
        txtConsultorio.setTextFormatter(new TextFormatter<>(cambio ->
                cambio.getControlNewText().matches("\\d{0," + MAX_DIGITOS_CONSULTORIO + "}") ? cambio : null));
    }

    /** Llena el formulario con los datos del veterinario (incluidos usuario y contraseña) para editarlo. */
    void editar(int id) {
        idVeterinario = id;
        lblTitulo.setText("🐾 Editar veterinario");
        btnGuardar.setText("Guardar cambios");

        Usuario u = service.obtenerUsuario(id);
        txtNombres.setText(u.getNombres());
        txtApellidos.setText(u.getApellidos());
        cmbSexo.setValue(u.getSexo());
        dpFechaNac.setValue(u.getFechaNacimiento());
        txtCorreo.setText(u.getEmail());
        txtTelefono.setText(u.getTelefono());
        txtCedula.setText(u.getCedula());
        txtConsultorio.setText(u.getNumConsultorio() == null ? "" : u.getNumConsultorio().toString());
        txtEspecialidad.setText(u.getEspecialidad());
        txtUsuario.setText(u.getNombreNegocio());
        txtPassword.setText(u.getPassword());
        txtConfirmPassword.setText(u.getPassword());
    }

    /** true si se creó el veterinario (para que la tabla del admin se recargue). */
    boolean isGuardado() {
        return guardado;
    }

    @FXML
    private void registrar(ActionEvent event) {
        Validacion.limpiar(lblMensaje, txtNombres, txtApellidos, txtCorreo, txtTelefono,
                txtCedula, txtConsultorio, txtEspecialidad, txtUsuario, txtPassword, txtConfirmPassword);

        if (Validacion.estaVacio(txtNombres)) {
            Validacion.error(lblMensaje, txtNombres, "Ingresa los nombres.");
            return;
        }
        if (Validacion.estaVacio(txtApellidos)) {
            Validacion.error(lblMensaje, txtApellidos, "Ingresa los apellidos.");
            return;
        }
        if (cmbSexo.getValue() == null) {
            Validacion.error(lblMensaje, "Selecciona el sexo.");
            return;
        }
        LocalDate fechaNac = dpFechaNac.getValue();
        if (fechaNac == null) {
            Validacion.error(lblMensaje, "Selecciona la fecha de nacimiento.");
            return;
        }
        if (Period.between(fechaNac, LocalDate.now()).getYears() < EDAD_MINIMA) {
            Validacion.error(lblMensaje, "El veterinario debe tener al menos " + EDAD_MINIMA + " años.");
            return;
        }
        if (Validacion.estaVacio(txtCorreo)) {
            Validacion.error(lblMensaje, txtCorreo, "Ingresa el correo electrónico.");
            return;
        }
        if (!Validacion.esCorreoValido(txtCorreo.getText())) {
            Validacion.error(lblMensaje, txtCorreo, "El correo electrónico no es válido.");
            return;
        }
        if (Validacion.estaVacio(txtTelefono)) {
            Validacion.error(lblMensaje, txtTelefono, "Ingresa el número de teléfono.");
            return;
        }
        if (Validacion.estaVacio(txtCedula)) {
            Validacion.error(lblMensaje, txtCedula, "Ingresa la cédula.");
            return;
        }
        if (!CEDULA.matcher(txtCedula.getText()).matches()) {
            Validacion.error(lblMensaje, txtCedula, "La cédula no tiene el formato correcto. Debe ser como "
                    + EJEMPLO_CEDULA + " (3 dígitos - 6 dígitos - 4 dígitos y una letra).");
            return;
        }
        if (Validacion.estaVacio(txtConsultorio)) {
            Validacion.error(lblMensaje, txtConsultorio, "Ingresa el número de consultorio.");
            return;
        }
        int consultorio = Integer.parseInt(txtConsultorio.getText());
        if (consultorio <= 0) {
            Validacion.error(lblMensaje, txtConsultorio, "El número de consultorio debe ser mayor que 0.");
            return;
        }
        if (Validacion.estaVacio(txtEspecialidad)) {
            Validacion.error(lblMensaje, txtEspecialidad, "Ingresa la especialidad.");
            return;
        }
        if (Validacion.estaVacio(txtUsuario)) {
            Validacion.error(lblMensaje, txtUsuario, "Ingresa un nombre de usuario.");
            return;
        }
        if (txtUsuario.getText().trim().length() < MIN_USUARIO) {
            Validacion.error(lblMensaje, txtUsuario,
                    "El nombre de usuario debe tener al menos " + MIN_USUARIO + " caracteres.");
            return;
        }
        if (Validacion.estaVacio(txtPassword)) {
            Validacion.error(lblMensaje, txtPassword, "Ingresa una contraseña.");
            return;
        }
        if (txtPassword.getText().length() < MIN_PASSWORD) {
            Validacion.error(lblMensaje, txtPassword,
                    "La contraseña debe tener al menos " + MIN_PASSWORD + " caracteres.");
            return;
        }
        if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {
            Validacion.error(lblMensaje, txtConfirmPassword, "Las contraseñas no coinciden.");
            return;
        }

        String usuario = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();
        String cedula = txtCedula.getText();

        Usuario datos = new Usuario();
        datos.setNombres(txtNombres.getText().trim());
        datos.setApellidos(txtApellidos.getText().trim());
        datos.setSexo(cmbSexo.getValue());
        datos.setFechaNacimiento(fechaNac);
        datos.setEmail(correo);
        datos.setTelefono(txtTelefono.getText().trim());
        datos.setCedula(cedula);
        datos.setNumConsultorio(consultorio);
        datos.setEspecialidad(txtEspecialidad.getText().trim());
        datos.setNombreNegocio(usuario);
        datos.setPassword(txtPassword.getText());

        try {
            // El usuario inicia sesión con este nombre, así que no puede repetirse
            if (service.nombreUsuarioEnUso(usuario, idVeterinario)) {
                Validacion.error(lblMensaje, txtUsuario, "Ese nombre de usuario ya está en uso.");
                return;
            }
            // Evita registrar dos veces el mismo correo (la columna email es unique)
            if (service.emailEnUso(correo, idVeterinario)) {
                Validacion.error(lblMensaje, txtCorreo, "Ya existe una cuenta con ese correo.");
                return;
            }

            if (service.cedulaEnUso(cedula, idVeterinario)) {
                Validacion.error(lblMensaje, txtCedula, "Ya existe un veterinario con esa cédula.");
                return;
            }

            if (idVeterinario == null) {
                service.registrarVeterinario(datos);
            } else {
                service.actualizarVeterinario(idVeterinario, datos);
            }
        } catch (RuntimeException ex) {
            Validacion.error(lblMensaje, idVeterinario == null
                    ? "No se pudo crear la cuenta. Intenta de nuevo."
                    : "No se pudieron guardar los cambios. Intenta de nuevo.");
            return;
        }

        guardado = true;
        cerrarVentana(event);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
