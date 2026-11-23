module com.proyectofinal.veterinarialosarcos {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;

    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    opens com.proyectofinal.veterinarialosarcos.models
            to org.hibernate.orm.core;
    opens com.proyectofinal.veterinarialosarcos.controllers
            to javafx.fxml;


    opens com.proyectofinal.veterinarialosarcos to javafx.fxml;
    exports com.proyectofinal.veterinarialosarcos;
    exports com.proyectofinal.veterinarialosarcos.controllers;
}