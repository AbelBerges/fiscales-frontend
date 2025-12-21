package org.desarrollo.fiscalesfrontend.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import javafx.scene.control.Button;
import java.io.IOException;
import java.net.URL;

public class EstablecimientoController {

    @FXML private StackPane contenedorCentral;
    @FXML private Button btnABM;
    @FXML private Button btnMesas;

    private Button botonActivo = new Button();

    @FXML
    public void initialize() {
        //Platform.runLater(() -> marcarBotonActivo(btnABM));
    }

    @FXML
    private void abrirABMEstablecimiento() {
        cargarVistaCentral("establecimiento_abm.fxml");
        marcarBotonActivo(btnABM);
    }
    @FXML
    private void abrirEstablecimientosMesas() {
        cargarVistaCentral("establecimientos_mesas.fxml");
        marcarBotonActivo(btnMesas);
    }

    private void marcarBotonActivo(Button nuevo) {
        if (nuevo == null) {
            return;
        }
        if (botonActivo != null) {
            botonActivo.getStyleClass().remove("boton-activo");
        }
        botonActivo = nuevo;
        if (!botonActivo.getStyleClass().contains("boton-activo")) {
            botonActivo.getStyleClass().add("boton-activo");
        }
    }


    private void cargarVistaCentral(String fxml) {
        try {
            String ruta = "/org/desarrollo/fiscalesfrontend/" + fxml;
            FXMLLoader carga = new FXMLLoader(getClass().getResource(ruta));
            //URL recurso = getClass().getResource(ruta);
            //Node nodo = carga.load();
            /*if (recurso == null) {
                throw new IllegalStateException("No se encontró la ruta " + ruta);
            }*/

            //FXMLLoader carga = new FXMLLoader(recurso);
            Node nodo = carga.load();
            contenedorCentral.getChildren().setAll(nodo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
