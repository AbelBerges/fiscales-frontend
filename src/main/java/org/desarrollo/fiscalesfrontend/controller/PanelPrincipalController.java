package org.desarrollo.fiscalesfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class PanelPrincipalController {

    @FXML private TabPane tabPane;

    @FXML
    public void initialize() {
        agregarModulo("Establecimiento", "establecimientos.fxml");
        agregarModulo("Fiscales", "fiscales.fxml");
        agregarModulo("Asignaciones", "asiganciones.fxml");
    }

    private void agregarModulo(String titulo, String fxml) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/org/desarrollo/fiscalesfrontend/" + fxml));
            Region vista = cargador.load();

            Tab tab = new Tab(titulo);
            tab.setContent(vista);
            tab.setClosable(false);

            tabPane.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
