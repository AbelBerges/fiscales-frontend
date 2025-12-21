package org.desarrollo.fiscalesfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class FiscalesController {

    @FXML private StackPane contenedorFiscales;
    @FXML private Button btnABMFiscal;
    private Button botonActivo = new Button();
    @FXML
    private void abrirABM() {
        cargarVistaInterna("fiscales_abm.fxml");
        marcarBotonActivo(btnABMFiscal);
    }


    private void marcarBotonActivo(Button activo) {
        if (activo == null) {
            return;
        }
        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("boton-activo");
        }
        botonActivo = activo;
        if (!botonActivo.getStyleClass().contains("boton-activo")) {
            botonActivo.getStyleClass().add("boton-activo");
        }
    }

    private void cargarVistaInterna(String fxml) {
        try {
            String ruta = "/org/desarrollo/fiscalesfrontend/" + fxml;
            URL recurso = getClass().getResource(ruta);
            if (recurso == null) {
                throw new IllegalStateException("No se encontró la ruta " + ruta);
            }
            FXMLLoader carga = new FXMLLoader(recurso);
            Node nodo = carga.load();
            contenedorFiscales.getChildren().setAll(nodo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
