package com.proyectofinal.veterinarialosarcos.utils;

import com.proyectofinal.veterinarialosarcos.models.Usuario;

/** Guarda el usuario que inició sesión para que todas las pantallas puedan usarlo. */
public final class Sesion {

    private static Usuario usuario;

    private Sesion() {
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void iniciar(Usuario usuarioActual) {
        usuario = usuarioActual;
    }

    public static void cerrar() {
        usuario = null;
    }
}
