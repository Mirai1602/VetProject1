package com.proyectofinal.veterinarialosarcos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource("Login.fxml")
                );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Veterinarialosarcos");
        stage.setScene(scene);
        stage.show();
    }
}
