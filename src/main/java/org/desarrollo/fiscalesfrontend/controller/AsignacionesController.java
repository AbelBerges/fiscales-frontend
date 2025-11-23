package org.desarrollo.fiscalesfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class AsignacionesController {

    @FXML private StackPane panelCentral;

    @FXML
    private void abrirAsignarFiscales() {
        cargarEnPanelCentral("asignar_fiscales.fxml");
    }

    private void cargarEnPanelCentral(String fxml) {
        try {
            String url = "/org/desarrollo/fiscalesfrontend/" + fxml;
            URL recurso = getClass().getResource(url);
            if (recurso == null) {
                throw new IllegalStateException("No se encontró la ruta al archivo " + url);
            }

            FXMLLoader carga = new FXMLLoader(recurso);
            Node nodo = carga.load();
            panelCentral.getChildren().setAll(nodo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
