module com.proyectofinal.veterinarialosarcos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;

    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jasperreports;
    // JasperReports no trae module-info: Java no carga sus dependencias por sí solo,
    // así que se declaran aquí (si no, falla al generar el PDF con NoClassDefFoundError).
    requires org.apache.commons.logging;
    requires org.apache.commons.beanutils;
    requires org.apache.commons.collections4;
    requires com.github.librepdf.openpdf;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.xml;

    opens com.proyectofinal.veterinarialosarcos.models
            to org.hibernate.orm.core;
    opens com.proyectofinal.veterinarialosarcos.controllers
            to javafx.fxml;


    opens com.proyectofinal.veterinarialosarcos to javafx.fxml;
    exports com.proyectofinal.veterinarialosarcos;
    exports com.proyectofinal.veterinarialosarcos.controllers;
}