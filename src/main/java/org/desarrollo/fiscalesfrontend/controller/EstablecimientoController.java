package org.desarrollo.fiscalesfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class EstablecimientoController {

    @FXML private StackPane contenedorCentral;

    @FXML
    private void abrirABMEstablecimiento() {
        cargarVistaCentral("establecimiento_abm.fxml");
    }
    @FXML
    private void abrirEstablecimientosMesas() {
        cargarVistaCentral("establecimientos_mesas.fxml");
    }


    private void cargarVistaCentral(String fxml) {
        try {
            String ruta = "/org/desarrollo/fiscalesfrontend/" + fxml;
            URL recurso = getClass().getResource(ruta);
            if (recurso == null) {
                throw new IllegalStateException("No se encontró la ruta " + ruta);
            }

            FXMLLoader carga = new FXMLLoader(recurso);
            Node nodo = carga.load();
            contenedorCentral.getChildren().setAll(nodo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
