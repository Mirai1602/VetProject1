package com.proyectofinal.veterinarialosarcos.enumerados;

/** Resultado de cada apartado del examen clínico. */
public enum EstadoRevision {
    NORMAL("Normal"),
    ANORMAL("Anormal"),
    NO_EXAMINADO("No examinado");

    private final String texto;

    EstadoRevision(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
