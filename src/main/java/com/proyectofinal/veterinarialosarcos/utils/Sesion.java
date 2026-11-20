package com.proyectofinal.veterinarialosarcos.utils;

import com.proyectofinal.veterinarialosarcos.models.Usuario;

/**
 * Guarda el usuario que inició sesión para que las demás pantallas sepan de quién son los datos.
 */
public final class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrar() {
        usuarioActual = null;
    }
}
