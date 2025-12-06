package org.desarrollo.fiscalesfrontend.controller;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.desarrollo.fiscalesfrontend.dto.*;
import org.desarrollo.fiscalesfrontend.mapper.EstablecimientoMapper;
import org.desarrollo.fiscalesfrontend.mapper.FiscalMapper;
import org.desarrollo.fiscalesfrontend.mapper.JornadaMapper;
import org.desarrollo.fiscalesfrontend.mapper.MesaMapper;
import org.desarrollo.fiscalesfrontend.model.*;
import org.desarrollo.fiscalesfrontend.service.*;
import org.desarrollo.fiscalesfrontend.validaciones.Validar;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class FiscalesABMConrtoller {

    @FXML private TextField txtNombreFiscal, txtApellidoFiscal, txtEdadFiscal, txtCorreoFiscal, txtTelefonoFiscal;
    @FXML private TextField campoBuscarCalle, apellidoBusqueda;
    @FXML private Label msgFiscalGuardado, labelMesa, campoMesa;
    @FXML private Label etiquetaId, txtValorId;
    @FXML private ComboBox<TipoPiso> elementoTipoPisoFiscal;
    @FXML private CheckBox chkActivoFiscal, chkFiscalActivoBusqueda;
    @FXML private ComboBox<Establecimiento> elementoEstablecimientoVota;
    @FXML private ComboBox<TipoDepartamento> elementoTipoDepartamento;
    @FXML private TextField txtAlturaDireccionFiscal;
    @FXML private ComboBox<TipoFiscal> elementoTipoFiscal;
    @FXML private ComboBox<TipoFiscal> tipoFiscalBusqueda;
    @FXML private ComboBox<Jornada> elementoJornada;
    @FXML private ComboBox<Jornada> jornadaBusqueda;
    @FXML private Button btnGuardarFiscal, btnactualizarFiscal, btnCancelarFiscal, btnBusquedaFiltros;
    @FXML private RadioButton todos, filtros;
    @FXML private CheckBox chkBusquedaTipoFiscal, chkBusquedaJornada, chkPorApellido;
    @FXML private GridPane gridFormulario;
    //Elementos de la tabla
    @FXML private TableView<Fiscal> tablaFiscales;
    @FXML private TableColumn<Fiscal, Integer> colIdFiscal;
    @FXML private TableColumn<Fiscal, String> colNombreFiscal;
    @FXML private TableColumn<Fiscal, String> colApellidoFiscal;
    @FXML private TableColumn<Fiscal, String> colJornada;
    @FXML private TableColumn<Fiscal, String> colMesa;
    @FXML private TableColumn<Fiscal, String> colEdadFiscal;
    @FXML private TableColumn<Fiscal, String> colCorreoFiscal;
    @FXML private TableColumn<Fiscal, String> colTelefonoFiscal;
    @FXML private TableColumn<Fiscal, String> colTipoFiscal;
    @FXML private TableColumn<Fiscal, Boolean> colActivoFiscal;
    @FXML private TableColumn<Fiscal, String> colCalleFiscal;
    @FXML private TableColumn<Fiscal, Integer> colAlturaFiscal;
    @FXML private TableColumn<Fiscal, String> colTipoPisoFiscal;
    @FXML private TableColumn<Fiscal, String> colTipoDeparatementoFiscal;
    @FXML private TableColumn<Fiscal, String> colEstablecimientoVota;

    //Declaramos los servicios
    private FiscalServicio servicio = new FiscalServicio();
    private CalleServicio calleServicio = new CalleServicio();
    private EstablecimientoServicio estServicio = new EstablecimientoServicio();
    private TipoEstablecimientoServicio tipoEstServicio = new TipoEstablecimientoServicio();
    private TipoPisoServicio tipoPisoServicio = new TipoPisoServicio();
    private TipoDepartamentoServicio tipoDeptoServicio = new TipoDepartamentoServicio();
    private TipoFiscalServicio tipoFiscalServicio = new TipoFiscalServicio();
    private JornadaServicio jornadaServicio = new JornadaServicio();
    private MesaServicio mesaServicio = new MesaServicio();


    private ObservableList<Fiscal> listaFiscales = FXCollections.observableArrayList();
    private SortedList<Fiscal> listaOrdenada;
    //Elementos necesarios para la búsqueda de calles.
    private List<Calle> listaCalles;
    private  List<Mesa> listaMesa;
    private List<Establecimiento> lstEstablecimientos;

    @FXML
    public void initialize() {
        gridFormulario.setMinWidth(620);
        GridPane.setHgrow(gridFormulario, Priority.ALWAYS);
        colIdFiscal.setCellValueFactory(new PropertyValueFactory<>("idFiscal"));
        colNombreFiscal.setCellValueFactory(new PropertyValueFactory<>("nombreFiscal"));
        colApellidoFiscal.setCellValueFactory(new PropertyValueFactory<>("apellidoFiscal"));
        colJornada.setCellValueFactory(celda -> {
            Jornada jornada = celda.getValue().getJornada();
            String tipo = jornada.getTipoJornada();
            return new ReadOnlyObjectWrapper<>(tipo);
        });
        colMesa.setCellValueFactory(celda -> {
            Mesa mesa = celda.getValue().getMesa();
            String numero = (mesa != null) ? String.valueOf(mesa.getNumeroMesa()) : "Sin asignar";
            return new ReadOnlyObjectWrapper<>(numero);
        });
        colEdadFiscal.setCellValueFactory(new PropertyValueFactory<>("edadFiscal"));
        colCorreoFiscal.setCellValueFactory(new PropertyValueFactory<>("correoFiscal"));
        colTelefonoFiscal.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEstablecimientoVota.setCellValueFactory(celda -> {
            if (celda.getValue().getEstablecimientoVotacion() != null) {
                Establecimiento est = celda.getValue().getEstablecimientoVotacion();
                String nomEst = est.getNombreEstablecimiento();
                return new ReadOnlyObjectWrapper<>(nomEst);
            } else {
                return new ReadOnlyObjectWrapper<>("Sin asignar");
            }
        });
        colTipoFiscal.setCellValueFactory(e -> {
            if (e.getValue().getTipoFiscal() != null) {
                TipoFiscal elTipo = e.getValue().getTipoFiscal();
                String nombre = elTipo.getNombre();
                return new ReadOnlyObjectWrapper<>(nombre);
            } else {
                return new ReadOnlyObjectWrapper<>("No tiene");
            }

        });
        colCalleFiscal.setCellValueFactory(e -> {
            Calle laCalle = e.getValue().getDireccion().getCalle();
            String nombreCalle = laCalle.getNombre();
            return new ReadOnlyObjectWrapper<>(nombreCalle);
        });
        colAlturaFiscal.setCellValueFactory(e -> {
            Integer altura = e.getValue().getDireccion().getAltura();
            return new ReadOnlyObjectWrapper<>(altura);
        });
        colTipoPisoFiscal.setCellValueFactory(e -> {
            if (e.getValue().getDireccion().getTipoPiso() != null) {
                TipoPiso tpf = e.getValue().getDireccion().getTipoPiso();
                String nombre = tpf.getNombre();
                return new ReadOnlyObjectWrapper<>(nombre);
            } else {
                return new ReadOnlyObjectWrapper<>("No tiene");
            }

        });
        colTipoDeparatementoFiscal.setCellValueFactory(e -> {
            if (e.getValue().getDireccion().getTipoDepartamento() != null) {
                TipoDepartamento tdepto = e.getValue().getDireccion().getTipoDepartamento();
                String nomDpto = tdepto.getNombre();
                return new ReadOnlyObjectWrapper<>(nomDpto);
            } else {
                return new ReadOnlyObjectWrapper<>("No tiene");
            }

        });
        colActivoFiscal.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colActivoFiscal.setCellFactory(col -> new TableCell<Fiscal, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                } else {
                    setText(activo ? "Sí" : "No");
                }
            }
        });
        //Cargamos las calles para toda la vista desde la base
        cargarListadoCalle();
        //Cargamos las calles para el domilicio
        campoBuscarCalle.setDisable(true);
        cargarCalles();
        //Configuramos el texto para la carga asíncrona de tipo de pisos
        elementoTipoPisoFiscal.setPromptText("Seleccione un piso");
        //Cargamos los tipos de pisos
        cargarListasEnComboBox(elementoTipoPisoFiscal, () -> tipoPisoServicio.listarTipoPiso());
        construirComBox(elementoTipoPisoFiscal, TipoPiso::getNombre, "Seleccione un piso");
        //configuramos el texto del combobox de tipo de departamento para la carga asíncrona
        elementoTipoDepartamento.setPromptText("Seleccione un departamento");
        //Cargamos los tipos de departamentos
        cargarListasEnComboBox(elementoTipoDepartamento, () -> tipoDeptoServicio.listarTiposDepartamentos());
        construirComBox(elementoTipoDepartamento, TipoDepartamento::getNombre, "Seleccione un departamento");
        //Configuramos el texto del combobox de tipo de fiscal para la carga asíncrona
        elementoTipoFiscal.setPromptText("Seleccione tipo Fiscal");
        //Cargampos los tipos de fiscales
        cargarListasEnComboBox(elementoTipoFiscal, () -> tipoFiscalServicio.listarTiposFiscales());
        construirComBox(elementoTipoFiscal, TipoFiscal::getNombre, "Seleccione tipo Fiscal");
        //Configuramos el texto que se muestra por defecto en el combobox jornada
        elementoJornada.setPromptText("Seleccione una jornada");
        cargarListasEnComboBox(elementoJornada, () -> jornadaServicio.listarJornadas());
        construirComBox(elementoJornada, Jornada::getTipoJornada, "Seleccione una jornada");
        //Elementos y listado donde vota el fiscal
        elementoEstablecimientoVota.setPromptText("Donde vota el fiscal");
        //cargarEstablecimientoVota();
        cargarListasEnComboBox(elementoEstablecimientoVota, () -> estServicio.listarEstablecimientos());
        construirComBox(elementoEstablecimientoVota, Establecimiento::getNombreEstablecimiento, "Donde vota el fiscal");
        //Tipos de fiscales para la búsqueda
        tipoFiscalBusqueda.setPromptText("Seleccionar");
        cargarListasEnComboBox(tipoFiscalBusqueda, () -> tipoFiscalServicio.listarTiposFiscales());
        construirComBox(tipoFiscalBusqueda, TipoFiscal::getNombre, "Seleccionar");

        //Las jornadas para los filtros de búsqueda
        jornadaBusqueda.setPromptText("Seleccionar");
        cargarListasEnComboBox(jornadaBusqueda, () -> jornadaServicio.listarJornadas());
        construirComBox(jornadaBusqueda, Jornada::getTipoJornada, "Seleccionar");

        //Cargamos los ficales en la tabla
        listaOrdenada = new SortedList<>(listaFiscales);
        listaOrdenada.comparatorProperty().bind(tablaFiscales.comparatorProperty());
        tablaFiscales.setItems(listaOrdenada);

        //Agregamos los listeners para habilitar botones de guardar y actualizar
        txtValorId.textProperty().addListener((obs, oldValue, newValue) -> habilitarActualizar());
        txtNombreFiscal.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        txtApellidoFiscal.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        txtEdadFiscal.textProperty().addListener(((observable, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        }));
        txtCorreoFiscal.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        txtTelefonoFiscal.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        elementoEstablecimientoVota.valueProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        elementoTipoFiscal.valueProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        campoBuscarCalle.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
            habilitarPisoDpto();
        });
        txtAlturaDireccionFiscal.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
            habilitarPisoDpto();
        });
        habilitarGuardar();
        habilitarActualizar();
        habilitarPisoDpto();
        //Elementos y controles de las búsquedas
        boolean filtroActivo = filtros.isSelected();
        setEstadoGrupoFiltro(filtroActivo);
        btnBusquedaFiltros.setDisable(true);
        filtros.selectedProperty().addListener((obs, ov, nv) -> {
            setEstadoGrupoFiltro(nv);
        });
        //Vinculamos cada checkbox con su control
        vincularCheckBoxYControl(chkBusquedaTipoFiscal, tipoFiscalBusqueda);
        vincularCheckBoxYControl(chkBusquedaJornada, jornadaBusqueda);
        vincularCheckBoxYControl(chkPorApellido, apellidoBusqueda);
        tipoFiscalBusqueda.valueProperty().addListener((obs, ov, nv) -> habilitarBotonBusquedaFiltro());
        jornadaBusqueda.valueProperty().addListener((obs, ov,nv) -> habilitarBotonBusquedaFiltro());
        apellidoBusqueda.textProperty().addListener((obs, ov, nv) -> habilitarBotonBusquedaFiltro());
        chkFiscalActivoBusqueda.selectedProperty().addListener((obs, ov, nv) -> habilitarBotonBusquedaFiltro());
        // Ajustamos el diseño de la distribución y ubicación de algunos elementos de la tabla
        tablaFiscales.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        colMesa.setStyle("-fx-alignment: CENTER;");
        colEdadFiscal.setStyle("-fx-alignment: CENTER;");
        colTipoPisoFiscal.setStyle("-fx-alignment: CENTER_RIGHT;");
        colTipoDeparatementoFiscal.setStyle("-fx-alignment: CENTER_RIGHT;");
        colActivoFiscal.setStyle("-fx-alignment: CENTER_RIGHT");
        colAlturaFiscal.setStyle("-fx-alignment: CENTER_RIGHT");

        tablaFiscales.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                cargarFiscalEnFormulario(newValue);
            }
        });
    }

    private void cargarFiscalEnFormulario(Fiscal fiscal) {
        etiquetaId.setText("ID");
        txtValorId.setText(String.valueOf(fiscal.getIdFiscal()));
        txtNombreFiscal.setText(fiscal.getNombreFiscal());
        txtApellidoFiscal.setText(fiscal.getApellidoFiscal());
        txtEdadFiscal.setText(String.valueOf(fiscal.getEdadFiscal()));
        txtCorreoFiscal.setText(fiscal.getCorreoFiscal());
        txtTelefonoFiscal.setText(fiscal.getTelefono());
        if (fiscal.getMesa() != null) {
            labelMesa.setText("Mesa");
            campoMesa.setText(String.valueOf(fiscal.getMesa().getNumeroMesa()));
        } else {
            labelMesa.setText("Mesa");
            campoMesa.setText("Sin asignación");
        }
        if (fiscal.getEstablecimientoVotacion() != null) {
            Integer votaId = fiscal.getEstablecimientoVotacion().getIdEstablecimiento();
            elementoEstablecimientoVota.getItems()
                    .stream()
                    .filter(vota -> vota != null && Objects.equals(vota.getIdEstablecimiento(), votaId))
                    .findFirst()
                    .ifPresent(elementoEstablecimientoVota::setValue);
        } else {
            elementoEstablecimientoVota.getSelectionModel().clearSelection();
        }
        if (fiscal.getTipoFiscal() != null) {
            Integer tfId = fiscal.getTipoFiscal().getIdTipoFiscal();
            elementoTipoFiscal.getItems()
                    .stream()
                    .filter(tf -> tf != null && Objects.equals(tf.getIdTipoFiscal(), tfId))
                    .findFirst()
                    .ifPresent(elementoTipoFiscal::setValue);
        } else {
            elementoTipoFiscal.getSelectionModel().clearSelection();
        }
        if (fiscal.getJornada() != null) {
            Integer id = fiscal.getJornada().getIdJornada();
            elementoJornada.getItems()
                    .stream()
                    .filter(j -> j != null && Objects.equals(j.getIdJornada(), id))
                    .findFirst()
                    .ifPresent(elementoJornada::setValue);
        } else {
            elementoJornada.getSelectionModel().clearSelection();
        }

        campoBuscarCalle.setText(fiscal.getDireccion().getCalle().getNombre());
        txtAlturaDireccionFiscal.setText(String.valueOf(fiscal.getDireccion().getAltura()));
        if (fiscal.getDireccion() != null && fiscal.getDireccion().getTipoPiso() != null) {
            Integer tpId = fiscal.getDireccion().getTipoPiso().getIdPiso();
            elementoTipoPisoFiscal.getItems()
                    .stream()
                    .filter(tpf -> tpf != null && Objects.equals(tpf.getIdPiso(), tpId))
                    .findFirst()
                    .ifPresent(elementoTipoPisoFiscal::setValue);
        } else {
            elementoTipoPisoFiscal.getSelectionModel().clearSelection();
        }
        if (fiscal.getDireccion() != null && fiscal.getDireccion().getTipoDepartamento() != null){
            Integer tipoDpto = fiscal.getDireccion().getTipoDepartamento().getIdDepartamento();
            elementoTipoDepartamento.getItems()
                    .stream()
                    .filter(dpto -> dpto != null && Objects.equals(dpto.getIdDepartamento(), tipoDpto))
                    .findFirst()
                    .ifPresent(elementoTipoDepartamento::setValue);
        } else {
            elementoTipoDepartamento.getSelectionModel().clearSelection();
        }
        chkActivoFiscal.setSelected(fiscal.isActivo());
        msgFiscalGuardado.setText("");

    }

    @FXML
    private void guardarFiscal() {
        Fiscal nuevo = new Fiscal();
        int edad = 0;
        try {
            edad = Integer.parseInt(txtEdadFiscal.getText().trim());
            if (!Validar.validarEdad(edad)) {
                mostrarAlerta("Error", "La edada debe ser entre 17 y 90 años", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "El número tiene que ser un entero", Alert.AlertType.ERROR);
        }

        String textoCalle = campoBuscarCalle.getText().trim();
        Calle seleccionada = listaCalles.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(textoCalle))
                .findFirst()
                .orElse(null);
        if (seleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una calle", Alert.AlertType.ERROR);
            return;
        }
        int altura = 0;
        String tomoAltura = txtAlturaDireccionFiscal.getText().trim();
        if (tomoAltura.isEmpty()) {
            mostrarAlerta("Error", "La altura de la dirección no puede estar vacía", Alert.AlertType.ERROR);
            return;
        }
        try {
            altura = Integer.parseInt(tomoAltura);
            if (!Validar.validarEnteroPositivo(altura)) {
                mostrarAlerta("Error", "La altura debe ser postiva", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La altura debe ser un número", Alert.AlertType.ERROR);
        }
        Integer idCalle = seleccionada.getIdCalle();
        Integer idTipoFiscal = elementoTipoFiscal.getValue().getIdTipoFiscal();
        Integer idJornada = elementoJornada.getValue() != null
                ? elementoJornada.getValue().getIdJornada()
                : null;
        //Integer idEstablecimientoVota = elementoEstablecimientoVota.getValue().getIdEstablecimiento();
        Integer idEstablecimientoVota = elementoEstablecimientoVota.getValue() != null
                ? elementoEstablecimientoVota.getValue().getIdEstablecimiento()
                : null;
        Integer idTipoPiso = elementoTipoPisoFiscal.getValue() != null
                ? elementoTipoPisoFiscal.getValue().getIdPiso()
                : null;
        Integer idTipoDpto = elementoTipoDepartamento.getValue() != null
                ? elementoTipoDepartamento.getValue().getIdDepartamento()
                : null;
        String nomFiscal = txtNombreFiscal.getText().toUpperCase(Locale.ROOT).trim();
        String apeFiscal = txtApellidoFiscal.getText().toUpperCase(Locale.ROOT).trim();
        Integer idMesa = null;
        FiscalRequestDTO crear = new FiscalRequestDTO(
                nomFiscal,
                apeFiscal,
                edad,
                txtCorreoFiscal.getText().trim(),
                txtTelefonoFiscal.getText().trim(),
                idTipoFiscal,
                chkActivoFiscal.isSelected(),
                idEstablecimientoVota,
                idCalle,
                altura,
                idTipoPiso,
                idTipoDpto,
                idJornada,
                null
        );
        //Hacemos el envío del POST
        Task<FiscalResponseDTO> tarea = new Task<FiscalResponseDTO>() {
            @Override
            protected FiscalResponseDTO call() throws Exception {
                return servicio.guardarFiscal(crear);
            }
        };
        tarea.setOnSucceeded(evento -> {
            msgFiscalGuardado.setText("El Fiscal se ha guardado correctamente");
            limpiarCampos();
            //Armamos el dto que devuelve la tarea
            FiscalResponseDTO dtoFiscal = tarea.getValue();
            //Creamos el fiscal
            Fiscal fiscal = FiscalMapper.aFiscalDeResponseDTO(dtoFiscal);
            Task<Fiscal> tareaEnriquecer = new Task<Fiscal>() {
                @Override
                protected Fiscal call() throws Exception {
                    return controlCamposTabla(fiscal);
                }
            };
            tareaEnriquecer.setOnSucceeded(e -> {
                Fiscal paraTabla = tareaEnriquecer.getValue();
                if (filtros.isSelected()) {
                    if (paraTabla.isActivo() != chkFiscalActivoBusqueda.isSelected()) {
                        opcionesBusquedaPorFiltro();
                        return;
                    }
                    if (tipoFiscalBusqueda.getValue() != null &&
                            !Objects.equals(tipoFiscalBusqueda.getValue().getIdTipoFiscal(), paraTabla.getTipoFiscal().getIdTipoFiscal())) {
                        opcionesBusquedaPorFiltro();
                        return;
                    }
                    if (jornadaBusqueda.getValue() != null &&
                            !Objects.equals(jornadaBusqueda.getValue().getIdJornada(), paraTabla.getJornada().getIdJornada())) {
                        opcionesBusquedaPorFiltro();
                        return;
                    }
                }
                listaFiscales.add(tareaEnriquecer.getValue());
                listaFiscales.sort(Comparator.comparing(f -> f.getApellidoFiscal() != null ? f.getApellidoFiscal() : null));
                tablaFiscales.setItems(listaFiscales);
            });
            tareaEnriquecer.setOnFailed(e ->  mostrarAlerta("Error", "Falló la carga de la tabla " + tareaEnriquecer.getException().getMessage(), Alert.AlertType.ERROR));
            new Thread(tareaEnriquecer).start();
        });
        tarea.setOnFailed(evento -> {
            Throwable err =tarea.getException();
            err.printStackTrace();
            mostrarAlerta("Error", "No se ha podido guardar el fiscal " + err.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();

    }

    @FXML
    private void actualizarFiscal() {
        String nuevoNombre = txtNombreFiscal.getText().toUpperCase(Locale.ROOT).trim();
        String nuevoApellido = txtApellidoFiscal.getText().toUpperCase(Locale.ROOT).trim();
        Integer tomoId = Integer.parseInt(txtValorId.getText());
        Integer nuevaEdad = 0;
        try {
            nuevaEdad = Integer.parseInt(txtEdadFiscal.getText());
            if (nuevaEdad < 18) {
                mostrarAlerta("Alerta", "La edad del fiscal debe ser mayor a 18 años", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "La edad tiene que ser un número", Alert.AlertType.ERROR);
        }
        String nuevoCorreo = txtCorreoFiscal.getText().trim();
        String nuevoTel = txtTelefonoFiscal.getText().trim();
        Integer nuevoVota = elementoEstablecimientoVota.getValue() != null
                ? elementoEstablecimientoVota.getValue().getIdEstablecimiento()
                : null;
        Integer nuevoTipoFiscal = elementoTipoFiscal.getValue() != null
                ? elementoTipoFiscal.getValue().getIdTipoFiscal()
                : null;
        Integer nuevaJornada = elementoJornada.getValue() != null
                ? elementoJornada.getValue().getIdJornada()
                : null;
        Boolean estado = chkActivoFiscal.isSelected();
        String textoCalle = campoBuscarCalle.getText();
        Calle nuevaCalle = listaCalles
                .stream()
                .filter(c -> c.getNombre().equals(textoCalle))
                .findFirst()
                .orElse(null);
        if (nuevaCalle == null) {
            mostrarAlerta("Error", "No se ha encontrado la calle", Alert.AlertType.ERROR);
        }
        Integer idNuevaCalle = nuevaCalle != null ? nuevaCalle.getIdCalle() : null;
        Integer nuevaAltura = 0;
        try {
            nuevaAltura = Integer.parseInt(txtAlturaDireccionFiscal.getText().trim());
            if (nuevaAltura < 1) {
                mostrarAlerta("Alerta", "La altura no puede ser negativa", Alert.AlertType.WARNING);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "La altura debe debe ser un número", Alert.AlertType.ERROR);
        }
        Integer nuevoTipoPiso = elementoTipoPisoFiscal.getValue() != null
                ? elementoTipoPisoFiscal.getValue().getIdPiso()
                : null;
        Integer nuevoTipoDpto = elementoTipoDepartamento.getValue() != null
                ? elementoTipoDepartamento.getValue().getIdDepartamento()
                : null;
        //Armamos el DTO para la actualización
        FiscalRequestDTO nuevoDto = new FiscalRequestDTO(
                nuevoNombre,
                nuevoApellido,
                nuevaEdad,
                nuevoCorreo,
                nuevoTel,
                nuevoTipoFiscal,
                estado,
                nuevoVota,
                idNuevaCalle,
                nuevaAltura,
                nuevoTipoPiso,
                nuevoTipoDpto,
                nuevaJornada,
                null
        );
        //Buscamos el fiscal antes de actualizar para saber el estado de activo true/flase
        Fiscal temp = new Fiscal();
        try {
            temp = servicio.buscoPorId(tomoId);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Creamos la tarea para la actualización en segundo plano
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return servicio.actualizoFiscal(tomoId, nuevoDto);
            }
        };
        //Si la tarea termina con exito
        Fiscal finalTemp = temp;
        tarea.setOnSucceeded(evento -> {
            msgFiscalGuardado.setText("Se ha actualizado el fiscal");
            //Evaluar como recargar la tabla con el dato actualizado
            FiscalResponseDTO crearDto = new FiscalResponseDTO(
                    tomoId,
                    nuevoDto.nombreFiscal(),
                    nuevoDto.apellidoFiscal(),
                    nuevoDto.edad(),
                    nuevoDto.correoFiscal(),
                    nuevoDto.telefono(),
                    nuevoDto.idTipoFiscal(),
                    nuevoDto.activo(),
                    nuevoDto.idEstablecimientoVotacion(),
                    nuevoDto.idCalle(),
                    nuevoDto.altura(),
                    nuevoDto.idTipoPiso(),
                    nuevoDto.idTipoDepartamento(),
                    nuevoDto.idJornada(),
                    null
            );
            Fiscal fiscal = FiscalMapper.aFiscalDeResponseDTO(crearDto);
            Task<Fiscal> enriquecer = new Task<Fiscal>() {
                @Override
                protected Fiscal call() throws Exception {
                    return controlCamposTabla(fiscal);
                }
            };
            enriquecer.setOnSucceeded(e -> {
                if (finalTemp.isActivo() != nuevoDto.activo()) {
                    opcionesBusquedaPorFiltro();
                    return;
                }
                if (tipoFiscalBusqueda.getValue() != null &&
                        !Objects.equals(tipoFiscalBusqueda.getValue().getIdTipoFiscal(), finalTemp.getTipoFiscal().getIdTipoFiscal())) {
                    opcionesBusquedaPorFiltro();
                    return;
                }
                if (jornadaBusqueda.getValue() != null &&
                        !Objects.equals(jornadaBusqueda.getValue().getIdJornada(), finalTemp.getJornada().getIdJornada())) {
                    opcionesBusquedaPorFiltro();
                    return;
                }
                if (!apellidoBusqueda.getText().isEmpty() &&
                        !Objects.equals(apellidoBusqueda.getText().toUpperCase(), finalTemp.getApellidoFiscal())) {
                    opcionesBusquedaPorFiltro();
                    return;
                }
                Fiscal actualizado = enriquecer.getValue();
                //Tomamos el índice en la lista
                int index = -1;
                for (int i = 0; i < listaFiscales.size(); i++) {
                    if (Objects.equals(listaFiscales.get(i).getIdFiscal(), actualizado.getIdFiscal())) {
                        index = i;
                        break;
                    }
                }
                //si lo encontró, lo reemplazamos. Si no, lo agrega
                if (index >= 0) {
                    listaFiscales.set(index, actualizado);
                }

            });
            enriquecer.setOnFailed(e -> mostrarAlerta("Error", "No se pudo recargar la tabla", Alert.AlertType.ERROR));
            new Thread(enriquecer).start();
            //cargarFiscalesTabla();
            limpiarCampos();
        });
        tarea.setOnFailed(evento -> {
            Throwable er = tarea.getException();
            mostrarAlerta("Error", "No se ha podido actualziar le fiscal " + er.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }


    private Fiscal controlCamposTabla(Fiscal datos) {
        try {
            if (datos.getDireccion() != null && datos.getDireccion().getCalle() != null) {
                Calle c = calleServicio.buscarPorId(datos.getDireccion().getCalle().getIdCalle());
                datos.getDireccion().setCalle(c);
            }
            if (datos.getDireccion() != null && datos.getDireccion().getTipoPiso() != null) {
                TipoPiso tp = tipoPisoServicio.buscarPorId(datos.getDireccion().getTipoPiso().getIdPiso());
                datos.getDireccion().setTipoPiso(tp);
            }
            if (datos.getDireccion() != null && datos.getDireccion().getTipoDepartamento() != null) {
                TipoDepartamento tpd = tipoDeptoServicio.buscarPorId(datos.getDireccion().getTipoDepartamento().getIdDepartamento());
                datos.getDireccion().setTipoDepartamento(tpd);
            }
            if (datos.getTipoFiscal() != null) {
                TipoFiscal tpf = tipoFiscalServicio.buscarPorID(datos.getTipoFiscal().getIdTipoFiscal());
                datos.setTipoFiscal(tpf);
            }
            if (datos.getEstablecimientoVotacion() != null && datos.getEstablecimientoVotacion().getIdEstablecimiento() != null) {
                EstablecimientoResponseDTO dto = estServicio.buscarPorId(datos.getEstablecimientoVotacion().getIdEstablecimiento());
                Establecimiento est = EstablecimientoMapper.aEntidadModelo(dto);
                datos.setEstablecimientoVotacion(est);
            }
            if (datos.getMesa() != null && datos.getMesa().getIdMesa() !=null) {
                MesaResponseDTO dto = mesaServicio.buscoMesaPorId(datos.getMesa().getIdMesa());
                Mesa mesa = MesaMapper.aEntidadCompleta(dto);
                datos.setMesa(mesa);
            }
            if (datos.getJornada() != null) {
                JornadaResponseDTO jdnDto = jornadaServicio.buscarPorId(datos.getJornada().getIdJornada());
                Jornada jornada = JornadaMapper.aEntidadCompleta(jdnDto);
                datos.setJornada(jornada);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al controlar los datos de la tabla", Alert.AlertType.ERROR);
        }
        return datos;
    }

    @FXML
    private void opcionesBusquedaPorFiltro() {
        Integer idTipoFiscal = tipoFiscalBusqueda.getValue() != null ? tipoFiscalBusqueda.getValue().getIdTipoFiscal() : null;
        Integer idJornada = jornadaBusqueda.getValue() != null ? jornadaBusqueda.getValue().getIdJornada() : null;
        Boolean activo = chkFiscalActivoBusqueda.isSelected();
        String apellido = apellidoBusqueda.getText();

        if (apellido != null && apellido.isEmpty()) {
            apellido = null;
        }
        cargarFiscalesTablaConFiltros(idTipoFiscal, idJornada, activo, apellido);
    }

    private void cargarFiscalesTablaConFiltros(Integer idTipoFiscal, Integer idJornada, Boolean activo, String apellido) {
        listaFiscales.clear();
        Task<List<Fiscal>> tarea = new Task<List<Fiscal>>() {
            @Override
            protected List<Fiscal> call() throws Exception {
                List<Fiscal> lista = servicio.listarFiscalesParaLasOpcionesDeFiltrado(idTipoFiscal, idJornada, activo, apellido);
                for (Fiscal fiscal : lista) {
                    controlCamposTabla(fiscal);
                }
                return lista;
            }
        };
        tarea.setOnSucceeded(evento -> {
            listaFiscales.setAll(tarea.getValue());
            listaFiscales.sort(Comparator.comparing(f -> f.getApellidoFiscal() != null ? f.getApellidoFiscal() : ""));
            tablaFiscales.setItems(listaFiscales);
        });
        tarea.setOnFailed(evento -> {
            mostrarAlerta("Error", "No se pudo recupera la lista con la búsqueda requerida para cargar la tabla " + tarea.getException().getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }

    @FXML
    private void cargarFiscalesTabla() {
        listaFiscales.clear();
        filtros.selectedProperty().set(false);
        setEstadoGrupoFiltro(filtros.isSelected());
        Task<List<Fiscal>> tarea = new Task<List<Fiscal>>() {
            @Override
            protected List<Fiscal> call() throws Exception {
                List<Fiscal> lista = servicio.listarFiscalesActivos();
                for (Fiscal fiscal : lista) {
                    //Buscamos y cargamos, si tiene, la calle en el objeto fiscal
                    controlCamposTabla(fiscal);
                }
                return lista;
            }
        };
        tarea.setOnSucceeded(event -> {
            listaFiscales.setAll(tarea.getValue());
            //Establecemos la lista ordenada para la tabla
            listaFiscales.sort(Comparator.comparing(f -> f.getApellidoFiscal() != null ? f.getApellidoFiscal() : ""));
            tablaFiscales.setItems(listaFiscales);
        });
        tarea.setOnFailed(evento -> mostrarAlerta("Error", "No se pudo recuperar la lista de fiscales al cargar la tabla" +
                tarea.getException(), Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    @FXML
    private void limpiarCampos() {
        etiquetaId.setText("");
        txtValorId.setText("");
        txtNombreFiscal.clear();
        txtApellidoFiscal.clear();
        txtEdadFiscal.clear();
        txtCorreoFiscal.clear();
        txtTelefonoFiscal.clear();
        txtAlturaDireccionFiscal.clear();
        campoBuscarCalle.clear();
        elementoTipoFiscal.getSelectionModel().clearSelection();
        elementoEstablecimientoVota.getSelectionModel().clearSelection();
        elementoTipoDepartamento.getSelectionModel().clearSelection();
        elementoTipoPisoFiscal.getSelectionModel().clearSelection();
        elementoJornada.getSelectionModel().clearSelection();
        chkActivoFiscal.setSelected(true);
        campoMesa.setText("");
        labelMesa.setText("");
    }

    private void cargarListadoCalle() {
        try {
            listaCalles = calleServicio.listarCalles();
        } catch (IOException e) {
            mostrarAlerta("Error", "IOExecption al listar las calles", Alert.AlertType.ERROR);
        } catch (InterruptedException e) {
            mostrarAlerta("Error", "InterruptedException al listar las calles", Alert.AlertType.ERROR);
        }
    }

    private <T> void cargarListasEnComboBox(ComboBox<T> combo, Callable<List<T>> proveedor) {
        Task<List<T>> tarea = new Task<List<T>>() {
            @Override
            protected List<T> call() throws Exception {
                return proveedor.call();
            }
        };
        tarea.setOnSucceeded(e -> combo.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(e -> mostrarAlerta("Error", "No se pudo cargar la lista ", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private <T> Comparator<T> ordenarListas(Function<T, String> mapper) {
        return Comparator.comparing(t -> normalizar(mapper.apply(t)),
                String.CASE_INSENSITIVE_ORDER);
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String nfd = java.text.Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}", ""); // elimina diacríticos (acentos)
    }

    private <T> void construirComBox(ComboBox<T> combo, Function<T, String> toStringMapper, String texto) {
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                //setText(empty || item == null ? null : toStringMapper.apply(item));
                if (empty) {
                    setText(null);
                } else if (item == null) {
                    setText("Todos");
                } else {
                    setText(toStringMapper.apply(item));
                }
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                //setText(!empty && item != null ? toStringMapper.apply(item) : texto);
                if (empty) {
                    setText(combo.getPromptText());
                } else if (item == null) {
                    setText("Todos");
                } else {
                    setText(toStringMapper.apply(item));
                }
            }
        });
    }



    //
    private void cargarCalles() {
        Task<List<Calle>> tarea = new Task<List<Calle>>() {
            @Override
            protected List<Calle> call() throws Exception {
                if (listaCalles == null) {
                    calleServicio.listarCalles();
                }
                return listaCalles;
            }
        };
        tarea.setOnSucceeded(evento -> {
            listaCalles = tarea.getValue();
            //Esperamos que el textfield esté listo visualmente
            Platform.runLater(() -> {
                if (campoBuscarCalle != null) {
                    configurarAutocompletarCalle(campoBuscarCalle, listaCalles);
                    campoBuscarCalle.setDisable(false);
                } else {
                    mostrarAlerta("Error", "El campo de Calle es nulo", Alert.AlertType.ERROR);
                }
            });
        });
        tarea.setOnFailed(evento -> {
            Throwable ex = tarea.getException();
            ex.printStackTrace();
            mostrarAlerta("Error", "No se pude cargar la lista de calles" + ex.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
    }

    //Este método es pora completar las calles del método cargarCalles()
    private void configurarAutocompletarCalle(TextField campoCalle, List<Calle> listado) {
        if (campoCalle == null || listado == null) {
            mostrarAlerta("Error", "El campo de búsqueda o la lista de calles está vacía", Alert.AlertType.ERROR);
            return;
        }
        //Armamos el contextMenu donde vamos a mostar las calles
        ContextMenu sugerencias = new ContextMenu();
        campoCalle.textProperty().addListener((obs, valorViejo, valorNuevo) -> {
            sugerencias.getItems().clear();
            if (valorNuevo ==null || valorNuevo.isBlank()) {
                sugerencias.hide();
                return;
            }
            String texto = valorNuevo.toLowerCase(Locale.ROOT);
            List<Calle> coincidencias = listado.stream()
                    .filter(c -> c.getNombre().toLowerCase(Locale.ROOT).contains(texto))
                    .limit(10)
                    .toList();
            if (coincidencias.isEmpty()) {
                sugerencias.hide();
                return;
            }
            for (Calle cl : coincidencias) {
                MenuItem item = new MenuItem(cl.getNombre());
                item.setOnAction(i -> {
                    campoCalle.setText(cl.getNombre());
                    sugerencias.hide();
                });
                sugerencias.getItems().add(item);
            }
            //Mostramos debajo del textfield
            if (!sugerencias.isShowing()) {
                sugerencias.show(campoCalle, Side.BOTTOM, 0, 0);
            }
        });
        //Validamos que si se pierde el foco: solo permite válidas
        campoCalle.focusedProperty().addListener((observable, osVal,newVal) -> {
            if (!newVal) {
                String texto = campoCalle.getText();
                boolean existe = listado.stream().anyMatch(e -> e.getNombre().equalsIgnoreCase(texto));
                if (!existe) {
                    campoCalle.clear();
                }
            }
        });
    }

    private void mostrarAlerta(String titulo, String msg, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(msg);
        alerta.showAndWait();
    }

    private void habilitarPisoDpto() {
        boolean obligatorios = !campoBuscarCalle.getText().isEmpty() &&
                !txtAlturaDireccionFiscal.getText().isEmpty();
        elementoTipoPisoFiscal.setDisable(!obligatorios);
        elementoTipoDepartamento.setDisable(!obligatorios);
    }

    /**
     * Habilita/deshabilita todo el bloque de filtros.
     * Si se deshabilita, también desmarca los CheckBoxes y limpia los controles asociados.
     */
    private void setEstadoGrupoFiltro(boolean activo) {
       //Habilita/deshabilita los checkboxes
        chkBusquedaTipoFiscal.setDisable(!activo);
        chkBusquedaJornada.setDisable(!activo);
        chkPorApellido.setDisable(!activo);
        chkFiscalActivoBusqueda.setDisable(!activo);
        if (!activo) {
            //desmarcamos los checkboxs
            chkBusquedaJornada.setSelected(false);
            chkBusquedaTipoFiscal.setSelected(false);
            chkPorApellido.setSelected(false);
            //Limpiamos y deshabilitamos los controles
            tipoFiscalBusqueda.getSelectionModel().clearSelection();
            jornadaBusqueda.getSelectionModel().clearSelection();
            apellidoBusqueda.clear();

            jornadaBusqueda.setDisable(true);
            tipoFiscalBusqueda.setDisable(true);
            apellidoBusqueda.setDisable(true);
        } else {
            //si se activa el grupo dejamos los controles según el estado de cada checkbox
            todos.selectedProperty().set(false);
            tipoFiscalBusqueda.setDisable(!chkBusquedaTipoFiscal.isSelected());
            jornadaBusqueda.setDisable(!chkBusquedaJornada.isSelected());
            apellidoBusqueda.setDisable(!chkPorApellido.isSelected());
            chkFiscalActivoBusqueda.setDisable(false);
        }
    }




    /**
     * Vincula de forma genérica un CheckBox con el control (ComboBox o TextField).
     * Cuando la checkbox cambia, habilita/deshabilita el control.
     * Si se desactiva la checkbox, limpia el valor del control.
     */
    private void vincularCheckBoxYControl(CheckBox chk, Node control) {
        //el estado inicial
        control.setDisable(!chk.isSelected());

        chk.selectedProperty().addListener((obs, ov, nv) -> {
            control.setDisable(!nv);
            if (!nv) {
                //Limpiamos el control
                if (control instanceof ComboBox<?> cb) {
                    cb.getSelectionModel().clearSelection();
                } else if (control instanceof TextField tf) {
                    tf.clear();
                }
            }
        });
    }

    private void habilitarBotonBusquedaFiltro() {
        boolean obligatorio = tipoFiscalBusqueda.getValue() != null ||
                jornadaBusqueda.getValue() != null ||
                !apellidoBusqueda.getText().isEmpty() ||
                !chkFiscalActivoBusqueda.isSelected();
        btnBusquedaFiltros.setDisable(!obligatorio);
    }

    private void habilitarGuardar() {
        boolean camposObligatorios = txtValorId.getText().isBlank() &&
                !txtNombreFiscal.getText().isEmpty() &&
                !txtApellidoFiscal.getText().isEmpty() &&
                !txtEdadFiscal.getText().isEmpty() &&
                !txtCorreoFiscal.getText().isEmpty() &&
                !txtTelefonoFiscal.getText().isEmpty() &&
                elementoTipoFiscal.getValue() != null &&
                !campoBuscarCalle.getText().isEmpty() &&
                !txtAlturaDireccionFiscal.getText().isEmpty();
        btnGuardarFiscal.setDisable(!camposObligatorios);
        if (camposObligatorios) {
            msgFiscalGuardado.setText("");
        }

    }

    private void habilitarActualizar() {
        boolean camposObligatorios = !txtValorId.getText().isBlank() &&
                txtValorId.getText() != null &&
                !txtNombreFiscal.getText().isEmpty() &&
                !txtApellidoFiscal.getText().isEmpty() &&
                !txtEdadFiscal.getText().isEmpty() &&
                !txtCorreoFiscal.getText().isEmpty() &&
                !txtTelefonoFiscal.getText().isEmpty() &&
                elementoTipoFiscal.getValue() !=null &&
                !campoBuscarCalle.getText().isEmpty() &&
                !txtAlturaDireccionFiscal.getText().isEmpty();
        btnactualizarFiscal.setDisable(!camposObligatorios);
        if (camposObligatorios) {
            msgFiscalGuardado.setText("");
        }

    }

}
