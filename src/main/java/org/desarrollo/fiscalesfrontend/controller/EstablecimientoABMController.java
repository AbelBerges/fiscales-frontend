package org.desarrollo.fiscalesfrontend.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoResponseDTO;
import org.desarrollo.fiscalesfrontend.mapper.EstablecimientoMapper;
import org.desarrollo.fiscalesfrontend.model.*;
import org.desarrollo.fiscalesfrontend.service.*;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    @FXML private TableView<Establecimiento> tablaEstablecimiento;
    @FXML private TableColumn<Establecimiento, Integer> colIdEstablecimiento;
    @FXML private TableColumn<Establecimiento, String> colNombreEstablecimiento;
    @FXML private TableColumn<Establecimiento, String> colDescripcionEstablecimiento;
    @FXML private TableColumn<Establecimiento, String> colTipoEstablecimiento;
    @FXML private TableColumn<Establecimiento, Integer> colCantidadMesas;
    @FXML private TableColumn<Establecimiento, String> colNombreCalleEstablecimiento;
    @FXML private TableColumn<Establecimiento, Integer> colAlturaEstablecimiento;
    @FXML private TableColumn<Establecimiento, String> colPisoEstablecimiento;
    @FXML private TableColumn<Establecimiento, String> colDepartamentoEstablecimiento;
    @FXML private TableColumn<Establecimiento, Boolean> colActivoEstableimiento;

    private ObservableList<Establecimiento> listadoEstablecimiento = FXCollections.observableArrayList();
    //Declaro e inicializo los servicios
    private CalleServicio servicioCalle = new CalleServicio();
    private EstablecimientoServicio servicio = new EstablecimientoServicio();
    private TipoEstablecimientoServicio tipoEstServicio = new TipoEstablecimientoServicio();
    private TipoDepartamentoServicio tipoDepartamentoServicio = new TipoDepartamentoServicio();
    private TipoPisoServicio tipoPisoServicio = new TipoPisoServicio();
    private List<Calle> listaOriginalCalles;
    //Controlar la recarga en el listener
    private boolean programmaticcChange = false;
    @FXML
    public void initialize() {
        //Iniciamos los elementos de la tabla
        colIdEstablecimiento.setCellValueFactory(
                celda -> new ReadOnlyObjectWrapper<>(celda.getValue().getIdEstablecimiento()));
        colNombreEstablecimiento.setCellValueFactory(
                celda -> new ReadOnlyObjectWrapper<>(celda.getValue().getNombreEstablecimiento()));
        colDescripcionEstablecimiento.setCellValueFactory(
                celda -> new ReadOnlyObjectWrapper<>(celda.getValue().getDescripcion()));
        colTipoEstablecimiento.setCellValueFactory(celda -> {
            TipoEstablecimiento t = celda.getValue().getTipoEstablecimiento();
            String tipo = t.getTipo();
            return new ReadOnlyObjectWrapper<>(tipo);
        });
        //colCantidadMesas.setCellValueFactory(celda -> new ReadOnlyObjectWrapper<>(celda.getValue().getMesas()));
        colNombreCalleEstablecimiento.setCellValueFactory(celda -> {
            Calle lacalle = celda.getValue().getDireccion().getCalle();
            String nombreCalle = lacalle.getNombre();
            return new ReadOnlyObjectWrapper<>(nombreCalle);
        });
        colAlturaEstablecimiento.setCellValueFactory(celda -> {
            Direccion dir = celda.getValue().getDireccion();
            Integer altura = dir.getAltura();
            return new ReadOnlyObjectWrapper<>(altura);
        });
        colPisoEstablecimiento.setCellValueFactory(celda -> {
            TipoPiso tipoPiso = celda.getValue().getDireccion().getTipoPiso();
            String nomTipoPiso = "";
            if (tipoPiso != null) {
                nomTipoPiso = tipoPiso.getNombre();
            } else {
                nomTipoPiso = "No tiene";
            }
            return new ReadOnlyObjectWrapper<>(nomTipoPiso);
        });
        colDepartamentoEstablecimiento.setCellValueFactory(celda -> {
            TipoDepartamento tipoDpto = celda.getValue().getDireccion().getTipoDepartamento();
            String nomTipoDpto = "";
            if (tipoDpto != null) {
                nomTipoDpto = tipoDpto.getNombre();
            } else {
                nomTipoDpto = "No tiene";
            }
            return new ReadOnlyObjectWrapper<>(nomTipoDpto);
        });
        colActivoEstableimiento.setCellValueFactory(celda ->
                new ReadOnlyObjectWrapper<>(celda.getValue().isActivo()));
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
        //Configuramos el inicio del combobox Piso
        tipoPisoEstablecimiento.setPromptText("Seleccione un Piso");
        cargarTipoPisos();
        //Configuramos el inicio del combobox de Departamento
        tipoDepartamentoEstablecimiento.setPromptText("Seleccione un Dpto");
        cargarTipoDepartamentos();
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

    private void cargarFormularioEstablecimiento(Establecimiento est) {
        etiquetaID.setText("ID");
        valorId.setText(String.valueOf(est.getIdEstablecimiento()));
        txtNombreEstablecimiento.setText(est.getNombreEstablecimiento());
        txtDescripcionEstablecimiento.setText(est.getDescripcion());
        tipoEstablecimiento.setValue(est.getTipoEstablecimiento());
        txtCalle.setText(est.getDireccion().getCalle().getNombre());
        txtAlturaEstablecimiento.setText(String.valueOf(est.getDireccion().getAltura()));
        if (est.getDireccion().getTipoPiso() != null && est.getDireccion().getTipoPiso().getIdPiso() != null) {
            Integer elId = est.getDireccion().getTipoPiso().getIdPiso();
            tipoPisoEstablecimiento.getItems().stream()
                    .filter(tp -> tp != null && Objects.equals(tp.getIdPiso(), elId))
                    .findFirst().ifPresent(tipoPisoEstablecimiento::setValue);
        } else {
            tipoPisoEstablecimiento.setValue(null);
        }
        if (est.getDireccion().getTipoDepartamento() != null && est.getDireccion().getTipoDepartamento().getIdDepartamento() != null) {
            Integer timoId = est.getDireccion().getTipoDepartamento().getIdDepartamento();
            tipoDepartamentoEstablecimiento.getItems()
                    .stream()
                    .filter(td -> td != null && Objects.equals(td.getIdDepartamento(), timoId))
                    .findFirst()
                    .ifPresent(tipoDepartamentoEstablecimiento::setValue);
        } else {
            tipoDepartamentoEstablecimiento.setValue(null);
        }
        activoEstablecimiento.setSelected(est.isActivo());
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
            Calle seleccion = listaOriginalCalles.stream()
                    .filter(c -> c.getNombre().equalsIgnoreCase(calleSeleccionada))
                    .findFirst()
                    .orElse(null);
            if (seleccion == null) {
                mostrarMensaje("Error", "No se ha encontrado la calle", Alert.AlertType.ERROR);
                return;
            }
            Integer idCalle = seleccion.getIdCalle();
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
            Task<EstablecimientoResponseDTO> tarea = new Task<EstablecimientoResponseDTO>() { //ver bien si de acá salta a la falla
                @Override
                protected EstablecimientoResponseDTO call() throws Exception {
                     return servicio.guardarEstablecimiento(dto);
                }
            };

            //Si la tarea es exitosa
            tarea.setOnSucceeded(evento -> {
                msgGrabacionExito.setText("El establecimiento se ha guardado con éxito");
                //Obtemos el DTO que devolvió la tarea
                EstablecimientoResponseDTO dtoDGuardado = tarea.getValue();
                //Convertimos el DTO a la entidad de dominio
                Establecimiento entidad = EstablecimientoMapper.aEntidadModelo(dtoDGuardado);
                Task<Establecimiento> enriquecer = new Task<Establecimiento>() {
                    @Override
                    protected Establecimiento call() throws Exception {
                        if (entidad.getDireccion() != null && entidad.getDireccion().getCalle() != null) {
                            Calle c = servicioCalle.buscarPorId(entidad.getDireccion().getCalle().getIdCalle());
                            entidad.getDireccion().setCalle(c);
                        }
                        if (entidad.getDireccion() != null && entidad.getDireccion().getTipoPiso() != null) {
                            TipoPiso tp = tipoPisoServicio.buscarPorId(entidad.getDireccion().getTipoPiso().getIdPiso());
                            entidad.getDireccion().setTipoPiso(tp);
                        }
                        if (entidad.getDireccion() != null && entidad.getDireccion().getTipoDepartamento() != null) {
                            TipoDepartamento tpDpto = tipoDepartamentoServicio.buscarPorId(entidad.getDireccion().getTipoDepartamento().getIdDepartamento());
                            entidad.getDireccion().setTipoDepartamento(tpDpto);
                        }
                        if (entidad.getTipoEstablecimiento() != null) {
                            TipoEstablecimiento tipoEst = tipoEstServicio.buscarPorId(entidad.getTipoEstablecimiento().getIdTipoEstablecimiento());
                            entidad.setTipoEstablecimiento(tipoEst);
                        }
                        return entidad;
                    }
                };
                enriquecer.setOnSucceeded(e -> {
                    listadoEstablecimiento.add(enriquecer.getValue());
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
        Calle buscoCalle = listaOriginalCalles.stream()
                .filter(c -> c.getNombre().equals(tomoCalle))
                .findFirst()
                .orElse(null);
        if (buscoCalle == null) {
            mostrarMensaje("Error", "No se ha encontrado la calle", Alert.AlertType.ERROR);
        }
        Integer elIdCalle = buscoCalle.getIdCalle();
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
            EstablecimientoResponseDTO crearDto = new EstablecimientoResponseDTO(
                    tomoId,
                    dto.nombre(),
                    dto.descripcion(),
                    dto.idCalle(),
                    dto.altura(),
                    dto.idTipoPiso(),
                    dto.idTipoDepartamento(),
                    dto.idTipoEstablecimiento(),
                    dto.activo()
            );
            Establecimiento establecimiento = EstablecimientoMapper.aEntidadModelo(crearDto);
            Task<Establecimiento> enriquecer = new Task<Establecimiento>() {
                @Override
                protected Establecimiento call() throws Exception {
                    if (establecimiento.getDireccion() != null && establecimiento.getDireccion().getCalle() != null) {
                        Calle c = servicioCalle.buscarPorId(establecimiento.getDireccion().getCalle().getIdCalle());
                        establecimiento.getDireccion().setCalle(c);
                    }
                    if (establecimiento.getDireccion() != null && establecimiento.getDireccion().getTipoPiso() != null) {
                        TipoPiso tp = tipoPisoServicio.buscarPorId(establecimiento.getDireccion().getTipoPiso().getIdPiso());
                        establecimiento.getDireccion().setTipoPiso(tp);
                    }
                    if (establecimiento.getDireccion() != null && establecimiento.getDireccion().getTipoDepartamento() != null) {
                        TipoDepartamento tpDpto = tipoDepartamentoServicio.buscarPorId(establecimiento.getDireccion().getTipoDepartamento().getIdDepartamento());
                        establecimiento.getDireccion().setTipoDepartamento(tpDpto);
                    }
                    if (establecimiento.getTipoEstablecimiento() != null) {
                        TipoEstablecimiento tpEst = tipoEstServicio.buscarPorId(establecimiento.getTipoEstablecimiento().getIdTipoEstablecimiento());
                        establecimiento.setTipoEstablecimiento(tpEst);
                    }
                    return establecimiento;
                }
            };
            enriquecer.setOnSucceeded(e -> {
                Establecimiento actualizado = enriquecer.getValue();
                int indice = -1;
                for (int i = 0; i < listadoEstablecimiento.size(); i++) {
                    if (listadoEstablecimiento.get(i).getIdEstablecimiento() == actualizado.getIdEstablecimiento()) {
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
        Task<List<Establecimiento>> tarea = new Task<List<Establecimiento>>() {
            @Override
            protected List<Establecimiento> call() throws Exception {
                //tablaEstablecimiemto
                List<Establecimiento> lista = servicio.listarEstablecimientos();
                //Salimos a buscar el nombre de las calles
                for (Establecimiento est : lista) {
                    //Construimos la calle
                    Calle c = servicioCalle.buscarPorId(est.getDireccion().getCalle().getIdCalle());
                    est.getDireccion().setCalle(c);
                    //Tomamos la dirección
                    Integer laAltura = est.getDireccion().getAltura();
                    //Si tiene Tipo de Piso lo traemos
                    if (est.getDireccion().getTipoPiso() != null) {
                        TipoPiso tp = tipoPisoServicio.buscarPorId(est.getDireccion().getTipoPiso().getIdPiso());
                        est.getDireccion().setTipoPiso(tp);
                    }
                    //si tiene departamento lo traemos
                    if (est.getDireccion().getTipoDepartamento() != null) {
                        TipoDepartamento tpd = tipoDepartamentoServicio.buscarPorId(est.getDireccion().getTipoDepartamento().getIdDepartamento());
                        est.getDireccion().setTipoDepartamento(tpd);
                    }
                    //Construimos el Tipo de Establecimiento
                    TipoEstablecimiento tep = tipoEstServicio.buscarPorId(est.getTipoEstablecimiento().getIdTipoEstablecimiento());

                    est.setTipoEstablecimiento(tep);

                }
                return lista;
            }
        };
        //tarea.setOnSucceeded(exito -> tablaEstablecimiento.getItems().setAll(tarea.getValue()));
        tarea.setOnSucceeded(exito -> {
            listadoEstablecimiento.setAll(tarea.getValue());
            //Ordenamos la lista una vez que tiene datos
            SortedList<Establecimiento> listaOrdenada = new SortedList<>(
                    listadoEstablecimiento,
                    Comparator.comparing(
                            est -> est.getNombreEstablecimiento() != null ? est.getNombreEstablecimiento() : "")
            );

            //Establecemos la lista ordenada como fuente
            tablaEstablecimiento.setItems(listaOrdenada);
            //Vinculamos el comparador con la tabla
            tablaEstablecimiento.comparatorProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    listaOrdenada.comparatorProperty().bind(tablaEstablecimiento.comparatorProperty());
                }
            });
        });
        tarea.setOnFailed(falla -> mostrarMensaje("Error", "No se pudo cargar la tabla" + tarea.getException().getMessage(), Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    //Cargamos las calles para la búsqueda
    private void cargarCalles() {
        Task<List<Calle>> tarea = new Task<List<Calle>>() {
            @Override
            protected List<Calle> call() throws Exception {
                return servicioCalle.listarCalles();
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

    private void configurarAutoCompletarCalle(TextField campoBusqueda, List<Calle> listaCalles) {
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
            List<Calle> coincidencias = listaCalles.stream()
                    .filter(c -> c.getNombre().toLowerCase(Locale.ROOT).contains(texto))
                    .limit(10)
                    .toList();
            if (coincidencias.isEmpty()) {
                menuSugerencia.hide();
                return;
            }
            for (Calle cl: coincidencias) {
                MenuItem item = new MenuItem(cl.getNombre());
                item.setOnAction(e -> {
                    campoBusqueda.setText(cl.getNombre());
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
                        .anyMatch(c -> c.getNombre().equalsIgnoreCase(texto));
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

        tarea.setOnSucceeded(evento -> tipoEstablecimiento.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarMensaje("Error", "No se pudo recuperar la lista de tipos de establecimientos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
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
