package org.desarrollo.fiscalesfrontend.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.desarrollo.fiscalesfrontend.dto.AsignacionMesasRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoMesasDTO;
import org.desarrollo.fiscalesfrontend.model.Establecimiento;
import org.desarrollo.fiscalesfrontend.model.Mesa;
import org.desarrollo.fiscalesfrontend.model.TipoEstablecimiento;
import org.desarrollo.fiscalesfrontend.service.EstablecimientoServicio;
import org.desarrollo.fiscalesfrontend.service.MesaServicio;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;


public class EstablecimientoMesas {

    @FXML private TextField campoNumeroMesa;
    @FXML private ComboBox<Establecimiento> comboEstablecimientos;
    @FXML private FlowPane contenedorMesas;
    @FXML private Label msjExito, etiquetaID, valorID, nombreEstablecimiento;
    @FXML private Button btnAgregarMesa, btnGuardarMesas, btnActualizarMesas, cancelarAsignacion;
    //Creamos los elementos de la tabla
    @FXML private TableView<EstablecimientoMesasDTO> tablaEstablecimientosMesas;
    @FXML private TableColumn<EstablecimientoMesasDTO, Integer> columnaIdEst;
    @FXML private TableColumn<EstablecimientoMesasDTO, Integer> columnaNombreEst;
    @FXML private TableColumn<EstablecimientoMesasDTO, Integer> columnaCantidadMesas;
    @FXML private TableColumn<EstablecimientoMesasDTO, Integer> columnaMesaInicial;
    @FXML private TableColumn<EstablecimientoMesasDTO, Integer> columnaMesaFinal;

    private EstablecimientoServicio servicioEst = new EstablecimientoServicio();
    private MesaServicio mesaServicio = new MesaServicio();
    private ObservableList<Mesa> mesasSeleccionadas = FXCollections.observableArrayList();
    private List<Mesa> listaMesas;
    private ObservableList<EstablecimientoMesasDTO> listadoMesas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        //Iniciamos los elementos de la tabla
        columnaIdEst.setCellValueFactory(new PropertyValueFactory<>("idEstablecimiento"));
        columnaNombreEst.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaCantidadMesas.setCellValueFactory(new PropertyValueFactory<>("cantidadMesas"));
        columnaMesaInicial.setCellValueFactory(new PropertyValueFactory<>("mesaInicial"));
        columnaMesaFinal.setCellValueFactory(new PropertyValueFactory<>("mesaFinal"));

        //inicializamos el valor por defecto que va en el combobox
        comboEstablecimientos.setPromptText("Seleccione un establecimiento");
        comboEstablecimientos.setButtonCell(new ListCell<Establecimiento>() {
            @Override
            protected void updateItem(Establecimiento item, boolean empty) {
                super.updateItem(item,empty);
                if (empty || item == null) {
                    setText(comboEstablecimientos.getPromptText());
                } else {
                    setText(item.getNombreEstablecimiento());
                }
            }
        });
        cargarEstablecimientos();

        // Construímos el campo número de mesas para la busqueda
        campoNumeroMesa.setDisable(true);
        campoNumeroMesa.setPromptText("Buscar el número de mesa");
        cargarMesas();

        //Cargamos los establecimientos al abrir la pestaña
        cargarTablaEstablecimientosMesas();
        //Ajustamos el diseño de la distribución y ubicación de algunos elementos de la tabla
        tablaEstablecimientosMesas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        columnaNombreEst.setMaxWidth(1f * Integer.MAX_VALUE);
        columnaCantidadMesas.setStyle("-fx-alignment: CENTER;");
        columnaMesaInicial.setStyle("-fx-alignment: CENTER_RIGHT;");
        columnaMesaFinal.setStyle("-fx-alignment: CENTER_RIGHT;");

        comboEstablecimientos.valueProperty().addListener((obs, ov, nv) -> {
            habilitarGrabarMesas();
            habilitarActualizar();
        });
        valorID.textProperty().addListener((obs, ov, nv) -> {
            habilitarGrabarMesas();
            habilitarActualizar();
        });
        mesasSeleccionadas.addListener((ListChangeListener<? super Mesa>) cambio -> {
            habilitarGrabarMesas();
            habilitarActualizar();
        });
        habilitarGrabarMesas();
        habilitarActualizar();

        //Agregamos un escuchador a la tabla para cargar los datos en el formulario
        tablaEstablecimientosMesas.getSelectionModel().selectedItemProperty().addListener((
                obs, ov, nv) -> {
            if (nv != null) {
                cargarDatosFormulario(nv);
            }
        });
    }

    private void cargarDatosFormulario(EstablecimientoMesasDTO datos) {
        limpiarCampos();
        try {
            AsignacionMesasRequestDTO dto = mesaServicio.mesasPorEstablecimiento(datos.getIdEstablecimiento());
            etiquetaID.setText("ID");
            valorID.setText(String.valueOf(datos.getIdEstablecimiento()));
            //System.out.println("a ver el nombre " + datos.getNombre());
            /*if (datos.getNombre() != null) {
                Integer elId = datos.getIdEstablecimiento();
                comboEstablecimientos.getItems()
                        .stream()
                        .filter(establecimiento -> establecimiento != null && Objects.equals(establecimiento.getIdEstablecimiento(), elId))
                        .findFirst()
                        .ifPresent(comboEstablecimientos::setValue);
            }*/
            nombreEstablecimiento.setText(datos.getNombre());
            comboEstablecimientos.setDisable(true);
            for (Integer i : dto.numerosMesa()) {
                Mesa temp = new Mesa();
                temp.setNumeroMesa(i);
                mesasSeleccionadas.add(temp);
                agregarFilaMesaVisual(temp);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void cargarTablaEstablecimientosMesas() {
        Task<List<EstablecimientoMesasDTO>> tarea = new Task<List<EstablecimientoMesasDTO>>() {
            @Override
            protected List<EstablecimientoMesasDTO> call() throws Exception {
                List<EstablecimientoMesasDTO> resultados = new ArrayList<>();
                EstablecimientoMesasDTO resumen = new EstablecimientoMesasDTO();
                for (Establecimiento e : servicioEst.listarEstablecimientosConMesas()) {
                    try {
                        resumen = servicioEst.mesasResumen(e.getIdEstablecimiento());
                        resultados.add(resumen);
                    } catch (Exception ex) {
                        mostarAlerta("Error", "No se pudo recuperar resumen para " + e.getNombreEstablecimiento() + ": " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                }
                return resultados;
            }
        };
        tarea.setOnSucceeded(evento -> {
            listadoMesas.setAll(tarea.getValue());
            SortedList<EstablecimientoMesasDTO> ordenada = new SortedList<>(
                    listadoMesas,
                    Comparator.comparing(
                            est -> est.getNombre() != null ? est.getNombre() : "")
            );
            tablaEstablecimientosMesas.setItems(ordenada);
        });
        tarea.setOnFailed(e -> {
            mostarAlerta("Error", "No se pudo completar la tabla " + tarea.getException().getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }

    private void cargarEstablecimientos() {
        Task<List<Establecimiento>> tarea = new Task<List<Establecimiento>>() {
            @Override
            protected List<Establecimiento> call() throws Exception {
                return servicioEst.listarEstablecimientosSinMesa();
            }
        };
        tarea.setOnSucceeded(evento -> {
            List<Establecimiento> lista = tarea.getValue();
            comboEstablecimientos.getItems().setAll(lista.stream().sorted(ordenarLista(Establecimiento::getNombreEstablecimiento)).toList());
        });
        String msg = tarea.getMessage();
        tarea.setOnFailed(evento -> mostarAlerta("Error", "No se pudo recuperar la lista de establecimiento" + msg, Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private <T> Comparator<T> ordenarLista(Function<T, String> mapper) {
        return Comparator.comparing(t-> normalizar(mapper.apply(t)),
                String.CASE_INSENSITIVE_ORDER);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String nfd = java.text.Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}", ""); // elimina diacríticos (acentos)
    }

    @FXML
    private void agregarMesa() {
        String textoMesa = campoNumeroMesa.getText();

        try {
            int numero = Integer.parseInt(textoMesa);
            //Validamos que la mesa no este repetida
            boolean yaExiste = mesasSeleccionadas.stream().anyMatch(m -> m.getNumeroMesa() == numero);
            if (yaExiste) {
                mostarAlerta("Aviso", "La mesa ya fue seleccionada", Alert.AlertType.INFORMATION);
                return;
            }
            Mesa nueva = new Mesa();
            nueva.setNumeroMesa(numero);
            mesasSeleccionadas.add(nueva);

            agregarFilaMesaVisual(nueva);
            campoNumeroMesa.clear();
            campoNumeroMesa.requestFocus();
        } catch (NumberFormatException e) {
            mostarAlerta("Error", "La mesa debe ser un número", Alert.AlertType.ERROR);
        }

    }

    @FXML
    private void guardarMesasSeleccionas(){
        List<Integer> numeros = mesasSeleccionadas.stream()
                .map(Mesa::getNumeroMesa)
                .toList();
        AsignacionMesasRequestDTO dto = new AsignacionMesasRequestDTO(
                comboEstablecimientos.getValue().getIdEstablecimiento(),
                numeros
        );
        Task<Void> tarea = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                mesaServicio.asignarMesas(dto);
                return null;
            }
        };
        tarea.setOnSucceeded(evento -> {
            //mandamos un mensaje en un label para ganar dinamismo
            msjExito.setText("Las mesas se han asignado correctamente");
            Task<EstablecimientoMesasDTO>  enriquecer = new Task<EstablecimientoMesasDTO>() {
                @Override
                protected EstablecimientoMesasDTO call() throws Exception {
                    String nomEst = comboEstablecimientos.getValue().getNombreEstablecimiento();
                    EstablecimientoMesasDTO paraTabla = new EstablecimientoMesasDTO();
                    paraTabla.setIdEstablecimiento(dto.idEstablecimiento());
                    paraTabla.setNombre(nomEst);
                    paraTabla.setCantidadMesas(dto.numerosMesa().size());
                    paraTabla.setMesaInicial(dto.numerosMesa().getFirst());
                    paraTabla.setMesaFinal(dto.numerosMesa().get(dto.numerosMesa().size() -1));
                    return paraTabla;
                }
            };
            enriquecer.setOnSucceeded(event -> {
                EstablecimientoMesasDTO asignado = enriquecer.getValue();
                //Buscamos identificar el establecimiento en la tabla
                Optional<EstablecimientoMesasDTO> existe = listadoMesas.stream()
                                .filter(m -> m.getIdEstablecimiento().equals(asignado.getIdEstablecimiento()))
                                .findFirst();
                if (existe.isPresent()) {
                    Integer index = listadoMesas.indexOf(existe.get());
                    listadoMesas.set(index, asignado);
                } else {
                    listadoMesas.add(asignado);
                }
                limpiarCampos();
            });
            enriquecer.setOnFailed(event ->
                    mostarAlerta("Error", "No se pudo cargar el registro a la tabla", Alert.AlertType.ERROR));
            new Thread(enriquecer).start();

        });
        tarea.setOnFailed(e -> {
            Throwable ex = tarea.getException();
            mostarAlerta("Error", "No se pudieron asignar las mesas al establecimiento " + ex.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }

    @FXML
    private void actualizarMesasAsignadas() {
        List<Integer> listado = mesasSeleccionadas.stream()
                .map(Mesa::getNumeroMesa)
                .toList();
        //Integer elId = comboEstablecimientos.getValue().getIdEstablecimiento();
        Integer elId = Integer.parseInt(valorID.getText());
        AsignacionMesasRequestDTO dto = new AsignacionMesasRequestDTO(elId, listado);
        Task<Void> tarea = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                mesaServicio.actualizarMesa(dto);
                return null;
            }
        };
        tarea.setOnSucceeded(evento -> {
            msjExito.setText("Las mesas se han actualizado correctamente");
            Task<EstablecimientoMesasDTO> enriquecer = new Task<EstablecimientoMesasDTO>() {
                @Override
                protected EstablecimientoMesasDTO call() throws Exception {
                    List<Integer> lista = dto.numerosMesa();
                    String nombre = comboEstablecimientos.getValue().getNombreEstablecimiento();
                    EstablecimientoMesasDTO temp = new EstablecimientoMesasDTO();
                    temp.setIdEstablecimiento(dto.idEstablecimiento());
                    temp.setNombre(nombre);
                    temp.setCantidadMesas(dto.numerosMesa().size());
                    temp.setMesaInicial(dto.numerosMesa().getFirst());
                    temp.setMesaFinal(dto.numerosMesa().getLast());
                    return temp;
                }
            };
            enriquecer.setOnSucceeded(e -> {
                EstablecimientoMesasDTO asignado = enriquecer.getValue();
                Optional<EstablecimientoMesasDTO> existe = listadoMesas.stream()
                        .filter(est -> est.getIdEstablecimiento().equals(asignado.getIdEstablecimiento()))
                        .findFirst();
                if (existe.isPresent()) {
                    Integer indice = listadoMesas.indexOf(existe.get());
                    listadoMesas.set(indice, asignado);
                }
                limpiarCampos();
            });
            enriquecer.setOnFailed(e -> mostarAlerta("Error", "No se pudo actualizar el dato en la tabla", Alert.AlertType.ERROR));
            new Thread(enriquecer).start();
        });
        tarea.setOnFailed(e -> mostarAlerta("Error", "No se pudo actualziar la asignación", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }


    private void cargarMesas() {
        Task<List<Mesa>> tareaMesa = new Task<List<Mesa>>() {
            @Override
            protected List<Mesa> call() throws Exception {
                return mesaServicio.mesasSinEstablecimiento();
            }
        };
        tareaMesa.setOnSucceeded(evento -> {
            listaMesas = (tareaMesa.getValue());
            //Trabajamos en segundo plano para esperar que el textfield esté listo
            Platform.runLater(() -> {
                if (campoNumeroMesa != null) {
                    autocompletarNumeroMesa(campoNumeroMesa, listaMesas);
                    campoNumeroMesa.setDisable(false);
                } else {
                    mostarAlerta("Error", "El némero de la mesa sigue siendo nulo ", Alert.AlertType.ERROR);
                }
            });
        });
        tareaMesa.setOnFailed(e -> {
            String msj = tareaMesa.getException().getMessage();
            mostarAlerta("Error", "No se pudo recuperar las mesas" + msj, Alert.AlertType.ERROR);
        });
        new Thread(tareaMesa).start();
    }

    private void autocompletarNumeroMesa(TextField numero, List<Mesa> listado) {
        if (numero == null || listado == null) {
            mostarAlerta("Error", "No se pudo configurar el auto completar: campo o lista nula", Alert.AlertType.ERROR);
            return;
        }

        ContextMenu sugerencia = new ContextMenu();
        campoNumeroMesa.textProperty().addListener((obs, valorViejo, valorNuevo) -> {
            sugerencia.getItems().clear();
            if (valorNuevo == null || valorNuevo.isBlank()) {
                sugerencia.hide();
                return;
            }
            //Integer num = Integer.parseInt(valorNuevo);
            List<Mesa> coincidencia = listado.stream()
                    .filter(m -> String.valueOf(m.getNumeroMesa()).startsWith(valorNuevo))
                    .limit(10)
                    .toList();
            if (coincidencia.isEmpty()) {
                sugerencia.hide();
                return;
            }
            for (Mesa m: coincidencia) {
                String temp = String.valueOf(m.getNumeroMesa());
                MenuItem item = new MenuItem(temp);
                item.setOnAction(evento -> {
                    campoNumeroMesa.setText(temp);
                    sugerencia.hide();
                });
                sugerencia.getItems().add(item);
            }
            //Mostramos debajo del TextField
            if (!sugerencia.isShowing()) {
                sugerencia.show(campoNumeroMesa, Side.BOTTOM, 0, 0);
            }
        });
        //Si perdemos foco validamos el contenido para que no haya valores inválidos
        campoNumeroMesa.focusedProperty().addListener((obs, valorViejo,balornuevo) -> {
            if (!balornuevo) {
                String texto = campoNumeroMesa.getText().trim();
                if (texto.isEmpty()){
                    return;
                }
            }
            try {
                if (!balornuevo) {
                    Integer tomoNum = Integer.parseInt(campoNumeroMesa.getText());
                    boolean exite = listaMesas.stream()
                            .anyMatch(m -> m.getNumeroMesa().equals(tomoNum));
                    if (!exite) {
                        campoNumeroMesa.clear();
                    }
                }
            } catch (NumberFormatException e) {
                mostarAlerta("Error", "La mesa debe ser un número existente", Alert.AlertType.ERROR);
                campoNumeroMesa.clear();
            }
        });
    }

    private void agregarFilaMesaVisual(Mesa mesa) {
        //Armamos un contenedor horizontal mesa más botón eliminar
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.TOP_LEFT);
        fila.setPadding(new Insets(5));
        Label lblMesa = new Label("Mesa " + mesa.getNumeroMesa());
        Button btnEliminar = new Button("Quitar");

        btnEliminar.setOnAction(a -> {
            mesasSeleccionadas.remove(mesa);
            contenedorMesas.getChildren().remove(fila);
        });

        fila.getChildren().addAll(lblMesa, btnEliminar);
        contenedorMesas.getChildren().add(fila);
    }

    private void mostarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void habilitarGrabarMesas() {
        boolean camposObligatorios = comboEstablecimientos.getValue() != null &&
                valorID.getText().isBlank() &&
                nombreEstablecimiento.getText().isBlank() &&
                !mesasSeleccionadas.isEmpty();
        btnGuardarMesas.setDisable(!camposObligatorios);
    }

    private void habilitarActualizar() {
        boolean obligaorios = !nombreEstablecimiento.getText().isBlank() &&
                !mesasSeleccionadas.isEmpty() &&
                !valorID.getText().isBlank();
        btnActualizarMesas.setDisable(!obligaorios);
    }

    @FXML
    private void limpiarCampos() {
        etiquetaID.setText("");
        valorID.setText("");
        nombreEstablecimiento.setText("");
        mesasSeleccionadas.clear();
        contenedorMesas.getChildren().clear();
        comboEstablecimientos.getSelectionModel().clearSelection();
        comboEstablecimientos.setValue(null);
        comboEstablecimientos.setPromptText("Seleccione un establecimiento");
        comboEstablecimientos.setDisable(false);
        //Forzamos la pérdida de foco del combo
        Platform.runLater(() -> {
            // el campo número de mesa debe estar habilitado porque no hay acciones para dehabilitarlo
            campoNumeroMesa.requestFocus();
        });
        campoNumeroMesa.requestFocus();
    }
}
