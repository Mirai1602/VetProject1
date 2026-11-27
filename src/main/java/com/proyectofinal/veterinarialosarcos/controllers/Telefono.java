package com.proyectofinal.veterinarialosarcos.controllers;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.List;

/**
 * Campo de teléfono con código de país: un ComboBox pequeño para el código (+505…)
 * y un TextField que solo acepta dígitos. Se guarda como "+505 88887777".
 */
final class Telefono {

    /** Código de país y cantidad de dígitos que lleva un número local. */
    record Pais(String nombre, String codigo, int digitos) {
        @Override
        public String toString() {
            return codigo + "  " + nombre;
        }
    }

    static final List<Pais> PAISES = List.of(
            new Pais("Nicaragua", "+505", 8),
            new Pais("Costa Rica", "+506", 8),
            new Pais("Honduras", "+504", 8),
            new Pais("El Salvador", "+503", 8),
            new Pais("Guatemala", "+502", 8),
            new Pais("Panamá", "+507", 8),
            new Pais("México", "+52", 10),
            new Pais("Estados Unidos", "+1", 10),
            new Pais("España", "+34", 9));

    private static final Pais POR_DEFECTO = PAISES.get(0);

    private Telefono() {
    }

    /** Llena el ComboBox de códigos y hace que el campo solo acepte dígitos. */
    static void configurar(ComboBox<Pais> cmbCodigo, TextField txtNumero) {
        cmbCodigo.getItems().setAll(PAISES);
        cmbCodigo.setValue(POR_DEFECTO);
        // En el botón solo se ve el código; en la lista desplegable, código + país.
        cmbCodigo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Pais pais, boolean vacio) {
                super.updateItem(pais, vacio);
                setText(vacio || pais == null ? null : pais.codigo());
            }
        });

        txtNumero.setTextFormatter(new TextFormatter<String>(cambio -> {
            // Al pegar "8888-7777" o "8888 7777" se quitan los caracteres que no son dígitos.
            if (cambio.isContentChange()) {
                cambio.setText(cambio.getText().replaceAll("\\D", ""));
            }
            Pais pais = cmbCodigo.getValue() == null ? POR_DEFECTO : cmbCodigo.getValue();
            return cambio.getControlNewText().length() <= pais.digitos() ? cambio : null;
        }));
        txtNumero.setPromptText("Teléfono (" + POR_DEFECTO.digitos() + " dígitos)");

        cmbCodigo.valueProperty().addListener((obs, anterior, pais) -> {
            if (pais == null) {
                return;
            }
            txtNumero.setPromptText("Teléfono (" + pais.digitos() + " dígitos)");
            String numero = txtNumero.getText();
            if (numero != null && numero.length() > pais.digitos()) {
                txtNumero.setText(numero.substring(0, pais.digitos()));
            }
        });
    }

    /** Muestra un teléfono guardado ("+505 88887777") separando el código y el número. */
    static void mostrar(ComboBox<Pais> cmbCodigo, TextField txtNumero, String guardado) {
        String texto = guardado == null ? "" : guardado.trim();
        Pais elegido = POR_DEFECTO;
        if (texto.startsWith("+")) {
            String codigo = texto.split("\\s+", 2)[0];
            for (Pais pais : PAISES) {
                if (pais.codigo().equals(codigo)) {
                    elegido = pais;
                    texto = texto.substring(codigo.length());
                    break;
                }
            }
        }
        cmbCodigo.setValue(elegido);
        String digitos = texto.replaceAll("\\D", "");
        txtNumero.setText(digitos.length() > elegido.digitos() ? digitos.substring(0, elegido.digitos()) : digitos);
    }

    /** Mensaje de error, o null si el teléfono está vacío (es opcional) o completo. */
    static String validar(ComboBox<Pais> cmbCodigo, TextField txtNumero) {
        String numero = txtNumero.getText() == null ? "" : txtNumero.getText();
        if (numero.isEmpty()) {
            return null;
        }
        Pais pais = cmbCodigo.getValue();
        if (pais == null) {
            return "Selecciona el código de país del teléfono.";
        }
        if (numero.length() != pais.digitos()) {
            return "El teléfono de " + pais.nombre() + " debe tener " + pais.digitos() + " dígitos.";
        }
        return null;
    }

    /** Teléfono listo para guardar ("+505 88887777"), o null si no se escribió. */
    static String valor(ComboBox<Pais> cmbCodigo, TextField txtNumero) {
        String numero = txtNumero.getText() == null ? "" : txtNumero.getText();
        if (numero.isEmpty()) {
            return null;
        }
        return cmbCodigo.getValue().codigo() + " " + numero;
    }
}
