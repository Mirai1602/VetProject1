package com.proyectofinal.veterinarialosarcos.enumerados;

public enum Sexo {
    MASCULINO("Masculino"),
    FEMENINO("Femenino");

    private final String texto;

    Sexo(String texto) {
        this.texto = texto;
    }

    //Así se muestra en tablas y ComboBox
    @Override
    public String toString() {
        return texto;
    }
}
