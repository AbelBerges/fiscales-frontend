package org.desarrollo.fiscalesfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panel-principal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1380, 700);
        stage.setTitle("Sistema de Administración de Fiscales");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
