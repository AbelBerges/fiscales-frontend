package org.desarrollo.fiscalesfrontend.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;

public class AsignacionesController {

    @FXML private StackPane panelCentral;
    @FXML private Button btnAsignarFiscales;
    @FXML private Button btnListarFiscalesEstablecimiento;
    private Button btnActivo = new Button();

    @FXML
    private void abrirAsignarFiscales() {
        cargarEnPanelCentral("asignar_fiscales.fxml");
        marcarBotonActivo(btnAsignarFiscales);
    }

    @FXML
    private void abrirListaPorEstablecimiento() {
        cargarEnPanelCentral("asignaciones_fiscales_establecimientos.fxml");
        marcarBotonActivo(btnListarFiscalesEstablecimiento);
    }

    private void marcarBotonActivo(Button activo) {
        if (activo == null)return;
        if (btnActivo != null) {
            btnActivo.getStyleClass().remove("boton-activo");
        }
        btnActivo = activo;
        if(!btnActivo.getStyleClass().contains("boton-activo")) {
            btnActivo.getStyleClass().add("boton-activo");
        }
    }

    private void cargarEnPanelCentral(String fxml) {
        try {
            String url = "/org/desarrollo/fiscalesfrontend/" + fxml;
            URL recurso = getClass().getResource(url);
            if (recurso == null) {
                throw new IllegalStateException("No se encontró la url " + url);
            }
            FXMLLoader carga = new FXMLLoader(recurso);
            Node nodo = carga.load();
            panelCentral.getChildren().setAll(nodo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
