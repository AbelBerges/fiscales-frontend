package org.desarrollo.fiscalesfrontend.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import org.desarrollo.fiscalesfrontend.dto.CalleMinimaDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoListaDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoResponseDTO;
import org.desarrollo.fiscalesfrontend.mapper.EstablecimientoMapper;
import org.desarrollo.fiscalesfrontend.model.*;
import org.desarrollo.fiscalesfrontend.service.*;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class EstablecimientoABMController {

    @FXML private Label msgGrabacionExito, etiquetaID, valorId;
    @FXML private TextField txtNombreEstablecimiento, txtDescripcionEstablecimiento, txtAlturaEstablecimiento, txtCalle, txtCantidadMesas;
    @FXML private ComboBox<TipoEstablecimiento> tipoEstablecimiento;
    @FXML private ComboBox<TipoPiso> tipoPisoEstablecimiento;
    @FXML private ComboBox<TipoDepartamento> tipoDepartamentoEstablecimiento;
    @FXML private CheckBox activoEstablecimiento;
    @FXML private Button btnGuardar;
    @FXML private Button btnActualiazar;
    @FXML private Button btnCancelar;

    //Elementos de la tabla
    @FXML private TableView<EstablecimientoListaDTO> tablaEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, Integer> colIdEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colNombreEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colDescripcionEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colTipoEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, Long> colCantidadMesas;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colNombreCalleEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, Integer> colAlturaEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colPisoEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colDepartamentoEstablecimiento;
    @FXML private TableColumn<EstablecimientoListaDTO, String> colActivoEstableimiento;

    private ObservableList<EstablecimientoListaDTO> listadoEstablecimiento = FXCollections.observableArrayList();
    //Declaro e inicializo los servicios
    private CalleServicio servicioCalle = new CalleServicio();
    private EstablecimientoServicio servicio = new EstablecimientoServicio();
    private TipoEstablecimientoServicio tipoEstServicio = new TipoEstablecimientoServicio();
    private TipoDepartamentoServicio tipoDepartamentoServicio = new TipoDepartamentoServicio();
    private TipoPisoServicio tipoPisoServicio = new TipoPisoServicio();
    private List<CalleMinimaDTO> listaOriginalCalles;
    //Controlar la recarga en el listener
    private boolean programmaticcChange = false;
    @FXML
    public void initialize() {
        //Iniciamos los elementos de la tabla
        colIdEstablecimiento.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(celda.getValue().idEstablecimiento()));
        colNombreEstablecimiento.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(celda.getValue().nombreEstablecimiento()));
        colDescripcionEstablecimiento.setCellValueFactory(
                celda -> new SimpleObjectProperty<>(celda.getValue().descripcion()));
        colTipoEstablecimiento.setCellValueFactory(celda -> {
            String tipo = celda.getValue().tipoEstablecimiento();
            return new SimpleObjectProperty<>(tipo);
        });
        colCantidadMesas.setCellValueFactory(celda -> new SimpleObjectProperty<>(celda.getValue().cantidadMesas()));
        colNombreCalleEstablecimiento.setCellValueFactory(celda -> {
            String nombreCalle = celda.getValue().calle();
            return new SimpleObjectProperty<>(nombreCalle);
        });
        colAlturaEstablecimiento.setCellValueFactory(celda -> {
            Integer altura = celda.getValue().altura();
            return new SimpleObjectProperty<>(altura);
        });
        colPisoEstablecimiento.setCellValueFactory(celda -> {
            String tipoPiso = celda.getValue().piso();
            String nomTipoPiso = "";
            if (tipoPiso != null) {
                nomTipoPiso = tipoPiso;
            } else {
                nomTipoPiso = "No tiene";
            }
            return new SimpleObjectProperty<>(nomTipoPiso);
        });
        colDepartamentoEstablecimiento.setCellValueFactory(celda -> {
            String tipoDpto = celda.getValue().departamento();
            String nomTipoDpto = "";
            if (tipoDpto != null) {
                nomTipoDpto = tipoDpto;
            } else {
                nomTipoDpto = "No tiene";
            }
            return new SimpleObjectProperty<>(nomTipoDpto);
        });
        colActivoEstableimiento.setCellValueFactory(celda -> new SimpleStringProperty(
                Boolean.TRUE.equals(celda.getValue().activo()) ? "Si" : "No"
        ));
        //Agregamos listeners para habilitar el botón guardar solo si los campos obligatorios están completos
        txtNombreEstablecimiento.textProperty().addListener((obs, ov, nv) -> {
            habilitarGrabar();
            habilitarActualizar();
        });
        txtCalle.textProperty().addListener((obs, ov, nv) -> {
            habilitarGrabar();
            habilitarActualizar();
        });
        txtAlturaEstablecimiento.textProperty().addListener((obs, ov, nv) -> {
            habilitarGrabar();
            habilitarActualizar();
        });
        tipoEstablecimiento.valueProperty().addListener((obs, ov, nv) -> {
            habilitarGrabar();
            habilitarActualizar();
        });
        valorId.textProperty().addListener((obs, ov,nv) -> habilitarActualizar());
        habilitarGrabar();
        habilitarActualizar();

        //Configuramos el inicio del combobox tipo de establecimiento
        tipoEstablecimiento.setPromptText("Seleccione un tipo");
        cargarTipoEstablecimiento();
        construirComobox(tipoEstablecimiento, TipoEstablecimiento::getTipo, "Seleccione un tipo");
        //Configuramos el inicio del combobox Piso
        tipoPisoEstablecimiento.setPromptText("Seleccione un Piso");
        cargarTipoPisos();
        construirComobox(tipoPisoEstablecimiento, TipoPiso::getNombre, "Seleccione un Piso");
        //Configuramos el inicio del combobox de Departamento
        tipoDepartamentoEstablecimiento.setPromptText("Seleccione un Dpto");
        cargarTipoDepartamentos();
        construirComobox(tipoDepartamentoEstablecimiento, TipoDepartamento::getNombre, "Seleccione un Dpto");
        //Cargamos la lista de calles
        txtCalle.setDisable(true);
        cargarCalles();
        //Ajustamos el diseño de la distribución y ubicación de algunos elementos de la tabla
        tablaEstablecimiento.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        colAlturaEstablecimiento.setStyle("-fx-alignment: CENTER_RIGHT;");
        colPisoEstablecimiento.setStyle("-fx-alignment: CENTER;");
        colDepartamentoEstablecimiento.setStyle("-fx-alignment: CENTER;");
        colActivoEstableimiento.setStyle("-fx-alignment: CENTER_RIGHT;");

        //Agregamos un escuchador a la tabla para que al seleccionar una fila se carguen los datos en el formulario
        tablaEstablecimiento.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (programmaticcChange) return; //ignorar cambios programáticos
                    if (newValue != null) {
                        cargarFormularioEstablecimiento(newValue);
                        msgGrabacionExito.setText("");
                    }
        });
        //cargamos los establecimientos en la tabla
        cargarEstablecimientosTabla();
    }

    private void cargarFormularioEstablecimiento(EstablecimientoListaDTO est) {
        etiquetaID.setText("ID");
        valorId.setText(String.valueOf(est.idEstablecimiento()));
        txtNombreEstablecimiento.setText(est.nombreEstablecimiento());
        txtDescripcionEstablecimiento.setText(est.descripcion());
        if (est.tipoEstablecimiento() != null) {
           tipoEstablecimiento.getItems().stream()
                   .filter(te -> te.getTipo() != null && Objects.equals(est.tipoEstablecimiento(), te.getTipo()))
                   .findFirst()
                   .ifPresent(tipoEstablecimiento::setValue);

        } else {
            tipoEstablecimiento.getSelectionModel().clearSelection();
        }
        txtCalle.setText(est.calle());
        txtAlturaEstablecimiento.setText(String.valueOf(est.altura()));
        if (est.piso() != null) {
            tipoPisoEstablecimiento.getItems().stream()
                    .filter(tp -> tp != null && Objects.equals(tp.getNombre(), est.piso()))
                    .findFirst().ifPresent(tipoPisoEstablecimiento::setValue);
        } else {
            tipoPisoEstablecimiento.getSelectionModel().clearSelection();
        }
        if (est.departamento() != null) {
            tipoDepartamentoEstablecimiento.getItems()
                    .stream()
                    .filter(td -> td != null && Objects.equals(td.getNombre(), est.departamento()))
                    .findFirst()
                    .ifPresent(tipoDepartamentoEstablecimiento::setValue);
        } else {
            tipoDepartamentoEstablecimiento.setValue(null);
        }
        activoEstablecimiento.setSelected(est.activo());
        msgGrabacionExito.setText("");
    }


    @FXML
    private void guardarEstablecimiento() {
        try {
            Integer nuevaAltura = 0;
            try {
                nuevaAltura = Integer.parseInt(txtAlturaEstablecimiento.getText().trim());
            } catch (NumberFormatException e) {
                mostrarMensaje("ERROR", "La altura no es un número valido", Alert.AlertType.ERROR);
                return;
            }
            //Buscar el ID de la calle seleccinada
            String calleSeleccionada = txtCalle.getText().trim();
            CalleMinimaDTO seleccion = listaOriginalCalles.stream()
                    .filter(c -> c.nombre().toUpperCase().contains(calleSeleccionada))
                    .findFirst()
                    .orElse(null);
            if (seleccion == null) {
                mostrarMensaje("Error", "No se ha encontrado la calle", Alert.AlertType.ERROR);
                return;
            }
            Integer idCalle = seleccion.idCalle();
            Integer idTipoEstablecimiento = tipoEstablecimiento.getValue().getIdTipoEstablecimiento();
            Integer idTipoPiso = tipoPisoEstablecimiento.getValue() != null
                    ? tipoPisoEstablecimiento.getValue().getIdPiso()
                    : null;
            Integer idTipoDepartamento = tipoDepartamentoEstablecimiento.getValue() != null
                    ? tipoDepartamentoEstablecimiento.getValue().getIdDepartamento()
                    : null;
            String nuevoNombre = txtNombreEstablecimiento.getText().toUpperCase(Locale.ROOT).trim();
            String laDescripcion = txtDescripcionEstablecimiento.getText().trim();
            //Crear el DTo para el POST
            EstablecimientoRequestDTO dto = new EstablecimientoRequestDTO(
                    nuevoNombre,
                    laDescripcion,
                    idCalle,
                    nuevaAltura,
                    idTipoPiso,
                    idTipoDepartamento,
                    idTipoEstablecimiento,
                    activoEstablecimiento.isSelected()
            );

            //Creamos la tarea en segundo plano
            Task<EstablecimientoListaDTO> tarea = new Task<EstablecimientoListaDTO>() { //ver bien si de acá salta a la falla
                @Override
                protected EstablecimientoListaDTO call() throws Exception {
                     return servicio.guardarEstablecimiento(dto);
                }
            };

            //Si la tarea es exitosa
            tarea.setOnSucceeded(evento -> {
                msgGrabacionExito.setText("El establecimiento se ha guardado con éxito");
                //Obtemos el DTO que devolvió la tarea
                EstablecimientoListaDTO dtoDGuardado = tarea.getValue();
                //Convertimos el DTO a la entidad de dominio
                Task<EstablecimientoListaDTO> enriquecer = new Task<EstablecimientoListaDTO>() {
                    @Override
                    protected EstablecimientoListaDTO call() throws Exception {
                        return dtoDGuardado;
                    }
                };
                enriquecer.setOnSucceeded(e -> {
                    listadoEstablecimiento.add(enriquecer.getValue());
                    listadoEstablecimiento.sort(Comparator.comparing(EstablecimientoListaDTO::nombreEstablecimiento));
                    //tablaEstablecimiento.refresh();
                    limpiarCampos();
                });
                enriquecer.setOnFailed(e -> mostrarMensaje("Error", "Error al cargar la tabla " + enriquecer.getException().getMessage(), Alert.AlertType.ERROR));
                new Thread(enriquecer).start();
            });
            tarea.setOnFailed(evento ->{
                Throwable ex = tarea.getException();
                ex.printStackTrace();
                mostrarMensaje("Error", "No se ha podido guardar el Establecimiento" + tarea.getException().getMessage(), Alert.AlertType.ERROR);
            });
            new Thread(tarea).start();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensaje("Error", "Ocurrió un error al guardar el establecimiento " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }




    @FXML
    private void actualizarEstablecimiento() {
        String nuevoNombre = txtNombreEstablecimiento.getText().toUpperCase().trim();
        String nuevaDescripcion = txtDescripcionEstablecimiento.getText() != null
                ? txtDescripcionEstablecimiento.getText().trim()
                : null;
        Integer elIdTipoEst = tipoEstablecimiento.getValue().getIdTipoEstablecimiento();
        Integer tomoId = Integer.parseInt(valorId.getText());
        Integer laAltura = 0;
        try {
            laAltura = Integer.parseInt(txtAlturaEstablecimiento.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "La altura debe ser un número entero " + e.getMessage(), Alert.AlertType.ERROR);
        }
        String tomoCalle = txtCalle.getText();
        CalleMinimaDTO buscoCalle = listaOriginalCalles.stream()
                .filter(c -> c.nombre().toUpperCase().contains(tomoCalle))
                .findFirst()
                .orElse(null);
        if (buscoCalle == null) {
            mostrarMensaje("Error", "No se ha encontrado la calle", Alert.AlertType.ERROR);
        }
        Integer elIdCalle = buscoCalle.idCalle();
        Integer elIdTipoPiso = tipoPisoEstablecimiento.getValue() != null
                ? tipoPisoEstablecimiento.getValue().getIdPiso()
                : null;
        Integer elIdTipoDpto = tipoDepartamentoEstablecimiento.getValue() != null
                ? tipoDepartamentoEstablecimiento.getValue().getIdDepartamento()
                : null;
        boolean estado = activoEstablecimiento.isSelected();
        //Creamos el objeto EstablecimientoRequestDTo
        EstablecimientoRequestDTO dto = new EstablecimientoRequestDTO(
                nuevoNombre,
                nuevaDescripcion,
                elIdCalle,
                laAltura,
                elIdTipoPiso,
                elIdTipoDpto,
                elIdTipoEst,
                estado
        );
        //Creamos la tarea para la actualización
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return servicio.actualizarEstablecimiento(tomoId, dto);
            }
        };
        //Si la tarea termina con exito
        tarea.setOnSucceeded(evento -> {
            msgGrabacionExito.setText("Se ha actualizado el establecimiento con exito");
            EstablecimientoListaDTO temp = null;
            try {
                temp = servicio.buscoPorIdOptimizado(tomoId);
            } catch (IOException | InterruptedException e) {
                mostrarMensaje("Error", "No se pudo recuperar el establecimiento por el id", Alert.AlertType.ERROR);
            }
            EstablecimientoListaDTO finalTemp = temp;
            Task<EstablecimientoListaDTO> enriquecer = new Task<EstablecimientoListaDTO>() {
                @Override
                protected EstablecimientoListaDTO call() throws Exception {
                    return finalTemp;
                }
            };
            enriquecer.setOnSucceeded(e -> {
                EstablecimientoListaDTO actualizado = enriquecer.getValue();
                int indice = -1;
                for (int i = 0; i < listadoEstablecimiento.size(); i++) {
                    if (Objects.equals(listadoEstablecimiento.get(i).idEstablecimiento(), actualizado.idEstablecimiento())) {
                        indice = i;
                        break;
                    }
                }
                if (indice >= 0) {
                    listadoEstablecimiento.set(indice, actualizado);
                } else {
                    listadoEstablecimiento.add(actualizado);
                }
            });
            enriquecer.setOnFailed(e -> mostrarMensaje("Error", "No se pudo actualizar la tabla ", Alert.AlertType.ERROR));
            new Thread(enriquecer).start();
            limpiarCampos();
        });
        tarea.setOnFailed(evento -> {
            Throwable err = tarea.getException();
            mostrarMensaje("Error", "Hubo un error al actualizar el establecimiento: " + err.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }

    private void actualizarTabla(List<Establecimiento> establecimientos, TableView<Establecimiento> us) {
        ObservableList<Establecimiento> datos = FXCollections.observableArrayList(establecimientos);
        us.setItems(datos);
    }

    private void cargarEstablecimientosTabla() {
        Task<List<EstablecimientoListaDTO>> tarea = new Task<List<EstablecimientoListaDTO>>() {
            @Override
            protected List<EstablecimientoListaDTO> call() throws Exception {
                return servicio.listarEstablecimientos();
            }
        };
        //tarea.setOnSucceeded(exito -> tablaEstablecimiento.getItems().setAll(tarea.getValue()));
        tarea.setOnSucceeded(exito -> {
            listadoEstablecimiento.setAll(tarea.getValue());
            //Ordenamos la lista una vez que tiene datos
            /*SortedList<Establecimiento> listaOrdenada = new SortedList<>(
                    listadoEstablecimiento,
                    Comparator.comparing(
                            est -> est.getNombreEstablecimiento() != null ? est.getNombreEstablecimiento() : "")
            );*/

            //Establecemos la lista ordenada como fuente
            tablaEstablecimiento.setItems(listadoEstablecimiento);
            //Vinculamos el comparador con la tabla
            /*tablaEstablecimiento.comparatorProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    listaOrdenada.comparatorProperty().bind(tablaEstablecimiento.comparatorProperty());
                }
            });*/
        });
        tarea.setOnFailed(falla -> mostrarMensaje("Error", "No se pudo cargar la tabla" + tarea.getException().getMessage(), Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    //Cargamos las calles para la búsqueda
    private void cargarCalles() {
        Task<List<CalleMinimaDTO>> tarea = new Task<List<CalleMinimaDTO>>() {
            @Override
            protected List<CalleMinimaDTO> call() throws Exception {
                return servicioCalle.recuperoCallePorNombre();
            }
        };
        tarea.setOnSucceeded(evento -> {
            listaOriginalCalles = tarea.getValue();
            //Esparamos a que el TextField esté listo visualmente
            Platform.runLater(() -> {
                if (txtCalle != null) {
                    configurarAutoCompletarCalle(txtCalle, listaOriginalCalles);
                    txtCalle.setDisable(false);
                } else {
                    mostrarMensaje("Error", "El txtCalle sigue siendo null", Alert.AlertType.ERROR);
                }
            });
        });
        tarea.setOnFailed(evento -> {
            Throwable ex = tarea.getException();
            ex.printStackTrace();
            mostrarMensaje("Error", "No se pudo recuperar la lista de calles" + ex.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }

    private void configurarAutoCompletarCalle(TextField campoBusqueda, List<CalleMinimaDTO> listaCalles) {
        if (campoBusqueda == null || listaCalles == null) {
            mostrarMensaje("Error", "No se pudo configurar el auto completar: campo o lista nula", Alert.AlertType.ERROR);
            return;
        }

        ContextMenu menuSugerencia = new ContextMenu();
        campoBusqueda.textProperty().addListener((obs, oldText, newText) -> {
            menuSugerencia.getItems().clear();
            if (newText == null || newText.isBlank()) {
                menuSugerencia.hide();
                return;
            }
            String texto = newText.toLowerCase(Locale.ROOT);
            List<CalleMinimaDTO> coincidencias = listaCalles.stream()
                    .filter(c -> c.nombre().toLowerCase(Locale.ROOT).contains(texto))
                    .limit(10)
                    .toList();
            if (coincidencias.isEmpty()) {
                menuSugerencia.hide();
                return;
            }
            for (CalleMinimaDTO cl: coincidencias) {
                MenuItem item = new MenuItem(cl.nombre());
                item.setOnAction(e -> {
                    campoBusqueda.setText(cl.nombre());
                    campoBusqueda.setUserData(cl);
                    menuSugerencia.hide();
                });
                menuSugerencia.getItems().add(item);
            }

            //Mostramos debajo del TextField
            if (!menuSugerencia.isShowing()) {
                menuSugerencia.show(campoBusqueda, Side.BOTTOM, 0 ,0);
            }
        });
        //Validamos al perder el foco: solo permitir calles válidas
        campoBusqueda.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String texto = campoBusqueda.getText();
                boolean existe = listaCalles.stream()
                        .anyMatch(c -> c.nombre().equalsIgnoreCase(texto));
                if (!existe) {
                    campoBusqueda.clear();
                }
            }
        });
    }

    private void cargarTipoEstablecimiento() {
        Task<List<TipoEstablecimiento>> tarea = new Task<List<TipoEstablecimiento>>() {
            @Override
            protected List<TipoEstablecimiento> call() throws Exception {
                return tipoEstServicio.listarTiposEstablecimiento();
            }
        };

        tarea.setOnSucceeded(evento -> {
            List<TipoEstablecimiento> lista = tarea.getValue();
            tipoEstablecimiento.getItems().setAll(lista.stream().sorted(ordenarLista(TipoEstablecimiento::getTipo)).toList());
        });
        tarea.setOnFailed(evento -> mostrarMensaje("Error", "No se pudo recuperar la lista de tipos de establecimientos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private <T> Comparator<T> ordenarLista (Function<T,String> mapper) {
        return Comparator.comparing(t -> normalizar(mapper.apply(t)),
                String.CASE_INSENSITIVE_ORDER);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String nfd = java.text.Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}", ""); // elimina diacríticos (acentos)
    }

    private void cargarTipoDepartamentos() {
        Task<List<TipoDepartamento>> tarea = new Task<List<TipoDepartamento>>() {
            @Override
            protected List<TipoDepartamento> call() throws Exception {
                return tipoDepartamentoServicio.listarTiposDepartamentos();
            }
        };
        tarea.setOnSucceeded(evento -> tipoDepartamentoEstablecimiento.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarMensaje("Error", "No se ha podido recuperar la lista de tipos de departamentos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private void cargarTipoPisos() {
        Task<List<TipoPiso>> tarea = new Task<List<TipoPiso>>() {
            @Override
            protected List<TipoPiso> call() throws Exception {
                return tipoPisoServicio.listarTipoPiso();
            }
        };

        tarea.setOnSucceeded(evento -> tipoPisoEstablecimiento.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarMensaje("Error", "No se pudo recupar la lista de tipos de pisos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private <T> void construirComobox(ComboBox<T> combo, Function<T, String> toStringMapper, String texto) {
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : toStringMapper.apply(item));
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(!empty && item != null ? toStringMapper.apply(item) : texto);
            }
        });
    }


    private void mostrarMensaje(String titulo, String msg, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }

    @FXML
    private void limpiarCampos() {
        etiquetaID.setText("");
        valorId.setText("");
        txtNombreEstablecimiento.clear();
        txtDescripcionEstablecimiento.clear();
        txtCalle.clear();
        txtAlturaEstablecimiento.clear();
        Platform.runLater(() -> {
            tipoEstablecimiento.getSelectionModel().clearSelection();
            tipoPisoEstablecimiento.getSelectionModel().clearSelection();
            tipoDepartamentoEstablecimiento.getSelectionModel().clearSelection();
            activoEstablecimiento.setSelected(true);
        });
    }

    private void habilitarGrabar() {
        boolean camposOblligatorios = !txtNombreEstablecimiento.getText().isEmpty() &&
                valorId.getText().isBlank() &&
                !txtCalle.getText().isEmpty() &&
                !txtAlturaEstablecimiento.getText().isEmpty() &&
                tipoEstablecimiento.getValue() != null;
        btnGuardar.setDisable(!camposOblligatorios);
        if (camposOblligatorios) {
            msgGrabacionExito.setText("");
        }
    }

    private void habilitarActualizar() {
        boolean obligatorios = valorId.getText() != null &&
                !valorId.getText().isBlank() &&
                !txtNombreEstablecimiento.getText().isEmpty() &&
                !txtCalle.getText().isEmpty() &&
                !txtAlturaEstablecimiento.getText().isEmpty() &&
                tipoEstablecimiento.getValue() != null;
        btnActualiazar.setDisable(!obligatorios);
        if (obligatorios) {
            msgGrabacionExito.setText("");
        }
    }
}
