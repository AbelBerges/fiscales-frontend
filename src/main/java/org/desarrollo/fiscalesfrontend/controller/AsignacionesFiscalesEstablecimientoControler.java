package org.desarrollo.fiscalesfrontend.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.desarrollo.fiscalesfrontend.dto.*;
import org.desarrollo.fiscalesfrontend.mapper.FiscalMapper;
import org.desarrollo.fiscalesfrontend.model.*;
import org.desarrollo.fiscalesfrontend.service.*;

import java.util.List;

public class AsignacionesFiscalesEstablecimientoControler {

    @FXML private ComboBox<EstablecimientoEstadoDTO> comboBoxEstablecimientos;
    @FXML private GridPane gridPaneTabla = new GridPane();

    @FXML private TableView<FiscalListaDTO> tablaFiscalesEstablecimientos;
    @FXML private TableColumn<FiscalListaDTO, Integer> colIdFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colNombreFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colApellidoFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colEstablecimientoVota;
    @FXML private TableColumn<FiscalListaDTO, String> colJornadaFiscal;
    @FXML private TableColumn<FiscalListaDTO, Integer> colNumeroMesa;
    @FXML private TableColumn<FiscalListaDTO, Integer> colEdadFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colCorreoFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colTelefonoFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colTipoFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colCalleNombre;
    @FXML private TableColumn<FiscalListaDTO, Integer> colAlturaCalleFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colPisoFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colDepartamentoFiscal;
    @FXML private TableColumn<FiscalListaDTO, String> colActivoFiscal;

    private EstablecimientoServicio estServicio = new EstablecimientoServicio();
    private FiscalServicio fiscalServicio = new FiscalServicio();

    @FXML
    public void initialize() {
        gridPaneTabla.setMinWidth(620);
        GridPane.setHgrow(gridPaneTabla, Priority.ALWAYS);
        colIdFiscal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().idFiscal()));
        colNombreFiscal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().nombreFiscal()));
        colApellidoFiscal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().apellidoFiscal()));
        colEstablecimientoVota.setCellValueFactory(celda -> {
            if (celda.getValue().establecimientoVoto() != null) {
                return new SimpleObjectProperty<>(celda.getValue().establecimientoVoto());
            } else {
                return new SimpleObjectProperty<>("Sin asignar");
            }
        });
        colJornadaFiscal.setCellValueFactory(celda -> {
            if (celda.getValue().jornada() != null) {
                return new SimpleStringProperty(celda.getValue().jornada());
            } else {
                return new SimpleStringProperty("Sin asignar");
            }
        });
        colNumeroMesa.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().numeroMesa()));
        colEdadFiscal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().edadFiscal()));
        colTipoFiscal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipoFiscal()));
        colCalleNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().calle()));
        colCorreoFiscal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().correoFiscal()));
        colTelefonoFiscal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().telefono()));
        colAlturaCalleFiscal.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().altura()));
        colPisoFiscal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().piso()));
        colDepartamentoFiscal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().departamento()));
        colActivoFiscal.setCellValueFactory(c -> new SimpleStringProperty(
                Boolean.TRUE.equals(c.getValue().activo()) ? "Sí" : "No"
        ));

        //Construimos el ComboBox de establecimientos
        comboBoxEstablecimientos.setPromptText("Seleccione un establecimiento");
        cargarEstablecimientoComboBox();
        comboBoxEstablecimientos.setCellFactory(e -> new ListCell<>() {
            @Override
            protected void updateItem(EstablecimientoEstadoDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(!empty || item != null ? item.nombreEstablecimiento() : null)  ;
            }
        });
        comboBoxEstablecimientos.setButtonCell(comboBoxEstablecimientos.getCellFactory().call(null));
        tablaFiscalesEstablecimientos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        colIdFiscal.setStyle("-fx-alignment: CENTER;");
        colApellidoFiscal.setStyle("-fx-alignment: CENTER_LEFT;");
        colNombreFiscal.setStyle("-fx-alignment: CENTER_LEFT;");
        colActivoFiscal.setStyle("-fx-alignment: CENTER;");
        colEstablecimientoVota.setStyle("-fx-alignment: CENTER_LEFT;");
        colTelefonoFiscal.setStyle("-fx-alignmenti: CENTER_RIGHT;");
        colJornadaFiscal.setStyle("-fx-alignment: CENTER;");
        colNumeroMesa.setStyle("-fx-alignment: CENTER_RIGHT;");
        colEdadFiscal.setStyle("-fx-alignment: CENTER_RIGHT;");
        colTipoFiscal.setStyle("-fx-alignment: CENTER;");
        colAlturaCalleFiscal.setStyle("-fx-alignment: CENTER_RIGHT;");
        colPisoFiscal.setStyle("-fx-alignment: CENTER;");
        colDepartamentoFiscal.setStyle("-fx-alignment: CENTER;");
        colActivoFiscal.setStyle("-fx-alignment: CENTER;");

        comboBoxEstablecimientos.valueProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                cargarFiscalesATabla(nv.idEstablecimiento());
            }
        });
    }

    private void cargarFiscalesATabla(Integer idEst) {
        Task<List<FiscalListaDTO>> tarea = new Task<List<FiscalListaDTO>>() {
            @Override
            protected List<FiscalListaDTO> call() throws Exception {
                return fiscalServicio.listarFiscalesPorEstablecimiento(idEst);
            }
        };
        tarea.setOnSucceeded(evento -> {
            List<FiscalListaDTO> lista = tarea.getValue();
            tablaFiscalesEstablecimientos.getItems().setAll(lista);
        });
        tarea.setOnFailed(evento -> mostrarMensaje("Error", "No se pudo cargar los fiscales en la tabla", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private void cargarEstablecimientoComboBox() {
        Task<List<EstablecimientoEstadoDTO>> tarea = new Task<List<EstablecimientoEstadoDTO>>() {
            @Override
            protected List<EstablecimientoEstadoDTO> call() throws Exception {
                return estServicio.listadoComoBoxAsignarFiscales();
            }
        };
        tarea.setOnSucceeded(evento -> {
            List<EstablecimientoEstadoDTO> lista = tarea.getValue();
            comboBoxEstablecimientos.getItems().setAll(lista);
        });
        tarea.setOnFailed(evento -> mostrarMensaje("Error", "No se pudo recuperar la lista de los establecimientos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
