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
import java.util.function.Function;

public class FiscalesABMConrtoller {

    @FXML private TextField txtNombreFiscal, txtApellidoFiscal, txtEdadFiscal, txtCorreoFiscal, txtTelefonoFiscal;
    @FXML private TextField campoBuscarCalle;
    @FXML private Label msgFiscalGuardado, campoMesa;
    @FXML private Label etiquetaId, txtValorId;
    @FXML private ComboBox<TipoPiso> elementoTipoPisoFiscal;
    @FXML private CheckBox chkActivoFiscal;
    @FXML private ComboBox<Establecimiento> elementoEstablecimientoVota;
    @FXML private ComboBox<TipoDepartamento> elementoTipoDepartamento;
    @FXML private TextField txtAlturaDireccionFiscal;
    @FXML private ComboBox<TipoFiscal> elementoTipoFiscal;
    @FXML private ComboBox<Jornada> elementoJornada;
    @FXML private Button btnGuardarFiscal, btnactualizarFiscal, btnCancelarFiscal;
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
            Establecimiento est = celda.getValue().getEstablecimientoVotacion();
            String nomEst = est.getNombreEstablecimiento();
            return new ReadOnlyObjectWrapper<>(nomEst);
        });
        colTipoFiscal.setCellValueFactory(e -> {
            TipoFiscal elTipo = e.getValue().getTipoFiscal();
            String nombre = elTipo.getNombre();
            return new ReadOnlyObjectWrapper<>(nombre);
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
            TipoPiso tpf = e.getValue().getDireccion().getTipoPiso();
            String nombre = tpf.getNombre();
            return new ReadOnlyObjectWrapper<>(nombre);
        });
        colTipoDeparatementoFiscal.setCellValueFactory(e -> {
            TipoDepartamento tdepto = e.getValue().getDireccion().getTipoDepartamento();
            String nomDpto = tdepto.getNombre();
            return new ReadOnlyObjectWrapper<>(nomDpto);
        });
        colActivoFiscal.setCellValueFactory(new PropertyValueFactory<>("activo"));
        //Cargamos las calles para toda la vista desde la base
        cargarListadoCalle();
        //Cargamos las calles para el domilicio
        campoBuscarCalle.setDisable(true);
        cargarCalles();
        //Configuramos el texto para la carga asíncrona de tipo de pisos
        elementoTipoPisoFiscal.setPromptText("Seleccione un piso");
        //Cargamos los tipos de pisos
        cargarTiposPisos();
        construirComBox(elementoTipoPisoFiscal, TipoPiso::getNombre, "Seleccione un piso");
        //configuramos el texto del combobox de tipo de departamento para la carga asíncrona
        elementoTipoDepartamento.setPromptText("Seleccione un departamento");
        //Cargamos los tipos de departamentos
        cargarTipoDepartamento();
        construirComBox(elementoTipoDepartamento, TipoDepartamento::getNombre, "Seleccione un departamento");
        //Configuramos el texto del combobox de tipo de fiscal para la carga asíncrona
        elementoTipoFiscal.setPromptText("Seleccione tipo Fiscal");
        //Cargampos los tipos de fiscales
        cargarTiposFiscales();
        construirComBox(elementoTipoFiscal, TipoFiscal::getNombre, "Seleccione tipo Fiscal");
        //Configuramos el texto que se muestra por defecto en el combobox jornada
        elementoJornada.setPromptText("Seleccione una jornada");
        //Cargamos las jornadas
        cargarJornadas();
        construirComBox(elementoJornada, Jornada::getTipoJornada, "Seleccione una jornada");
        //Elementos y listado donde vota el fiscal
        elementoEstablecimientoVota.setPromptText("Donde vota el fiscal");
        cargarEstablecimientoVota();
        construirComBox(elementoEstablecimientoVota, Establecimiento::getNombreEstablecimiento, "Donde vota el fiscal");
        //Cargamos los ficales en la tabla
        tablaFiscales.setItems(listaFiscales);
        cargarFiscalesTabla();
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
        });
        txtAlturaDireccionFiscal.textProperty().addListener((obs, oldValue, newValue) -> {
            habilitarGuardar();
            habilitarActualizar();
        });
        habilitarGuardar();
        habilitarActualizar();
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
            campoMesa.setText(String.valueOf(fiscal.getMesa().getNumeroMesa()));
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
        Integer idEstablecimientoVota = elementoEstablecimientoVota.getValue().getIdEstablecimiento();
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
                    if (fiscal.getDireccion() != null && fiscal.getDireccion().getCalle() != null) {
                        Calle c = calleServicio.buscarPorId(fiscal.getDireccion().getCalle().getIdCalle());
                        fiscal.getDireccion().setCalle(c);
                    }
                    if (fiscal.getDireccion() != null && fiscal.getDireccion().getTipoPiso() != null) {
                        TipoPiso tp = tipoPisoServicio.buscarPorId(fiscal.getDireccion().getTipoPiso().getIdPiso());
                        fiscal.getDireccion().setTipoPiso(tp);
                    }
                    if (fiscal.getDireccion() != null && fiscal.getDireccion().getTipoDepartamento() != null) {
                        TipoDepartamento tpDpto = tipoDeptoServicio.buscarPorId(fiscal.getDireccion().getTipoDepartamento().getIdDepartamento());
                        fiscal.getDireccion().setTipoDepartamento(tpDpto);
                    }
                    if (fiscal.getTipoFiscal() != null) {
                        TipoFiscal tipoFiscal = tipoFiscalServicio.buscarPorID(fiscal.getTipoFiscal().getIdTipoFiscal());
                        fiscal.setTipoFiscal(tipoFiscal);
                    }
                    if (fiscal.getEstablecimientoVotacion() != null) {
                        EstablecimientoResponseDTO dto = estServicio.buscarPorId(fiscal.getEstablecimientoVotacion().getIdEstablecimiento());
                        Establecimiento est = EstablecimientoMapper.aEntidadModelo(dto);
                        fiscal.setEstablecimientoVotacion(est);
                    }
                    if (fiscal.getJornada() != null) {
                        JornadaResponseDTO dto = jornadaServicio.buscarPorId(fiscal.getJornada().getIdJornada());
                        Jornada jornada = JornadaMapper.aEntidadCompleta(dto);
                        fiscal.setJornada(jornada);
                    }
                    return fiscal;
                }
            };
            tareaEnriquecer.setOnSucceeded(e -> {
                listaFiscales.add(tareaEnriquecer.getValue());
            });
            tareaEnriquecer.setOnFailed(e ->  mostrarAlerta("Error", "Falló la carga de la tabla " + tareaEnriquecer.getException().getMessage(), Alert.AlertType.ERROR));
            new Thread(tareaEnriquecer).start();
        });
        tarea.setOnFailed(evento -> {
            Throwable err =tarea.getException();
            mostrarAlerta("Error", "No se ha podido guardar el fisfal " + err.getMessage(), Alert.AlertType.ERROR);
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
        boolean estado = chkActivoFiscal.isSelected();
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
        /*Integer numMesa = Integer.parseInt(campoMesa.getText());
        Mesa laMesa = listaMesa
                .stream()
                .filter(m -> m.getNumeroMesa().equals(numMesa))
                .findFirst()
                .orElse(null);
        if (laMesa == null) {
            mostrarAlerta("Error", "No se ha encontrado la calle", Alert.AlertType.ERROR);
        }
        Integer idMesa = laMesa != null ? laMesa.getIdMesa() : null;*/
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
        //Creamos la tarea para la actualización en segundo plano
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return servicio.actualizoFiscal(tomoId, nuevoDto);
            }
        };
        //Si la tarea termina con exito
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
                    if (fiscal.getDireccion() != null && fiscal.getDireccion().getCalle() != null) {
                        Calle c = calleServicio.buscarPorId(fiscal.getDireccion().getCalle().getIdCalle());
                        fiscal.getDireccion().setCalle(c);
                    }
                    if (fiscal.getDireccion() != null && fiscal.getDireccion().getTipoPiso() != null) {
                        TipoPiso tp = tipoPisoServicio.buscarPorId(fiscal.getDireccion().getTipoPiso().getIdPiso());
                        fiscal.getDireccion().setTipoPiso(tp);
                    }
                    if (fiscal.getDireccion() != null && fiscal.getDireccion().getTipoDepartamento() != null) {
                        TipoDepartamento tpd = tipoDeptoServicio.buscarPorId(fiscal.getDireccion().getTipoDepartamento().getIdDepartamento());
                        fiscal.getDireccion().setTipoDepartamento(tpd);
                    }
                    if (fiscal.getTipoFiscal() != null) {
                        TipoFiscal tpf = tipoFiscalServicio.buscarPorID(fiscal.getTipoFiscal().getIdTipoFiscal());
                        fiscal.setTipoFiscal(tpf);
                    }
                    if (fiscal.getEstablecimientoVotacion() != null) {
                        EstablecimientoResponseDTO dto = estServicio.buscarPorId(fiscal.getEstablecimientoVotacion().getIdEstablecimiento());
                        Establecimiento est = EstablecimientoMapper.aEntidadModelo(dto);
                        fiscal.setEstablecimientoVotacion(est);
                    }
                    if (fiscal.getJornada() != null) {
                        JornadaResponseDTO jdnDto = jornadaServicio.buscarPorId(fiscal.getJornada().getIdJornada());
                        Jornada jornada = JornadaMapper.aEntidadCompleta(jdnDto);
                        fiscal.setJornada(jornada);
                    }
                    /*if (fiscal.getMesa() != null) {
                        MesaResponseDTO msDto = mesaServicio.buscoMesaPorId(fiscal.getMesa().getIdMesa());
                        Mesa mesa = MesaMapper.aEntidadCompleta(msDto);
                        fiscal.setMesa(mesa);
                    }*/
                    return fiscal;
                }
            };
            enriquecer.setOnSucceeded(e -> {
                Fiscal actualizado = enriquecer.getValue();
                //Tomamos el índice en la lista
                int index = -1;
                for (int i = 0; i < listaFiscales.size(); i++) {
                    if (listaFiscales.get(i).getIdFiscal() == actualizado.getIdFiscal()) {
                        index = i;
                        break;
                    }
                }
                //si lo encontró, lo reemplazamos. Si no, lo agrega
                if (index >= 0) {
                    listaFiscales.set(index, actualizado);
                } else {
                    listaFiscales.add(actualizado);
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

    private void cargarFiscalesTabla() {
        Task<List<Fiscal>> tarea = new Task<List<Fiscal>>() {
            @Override
            protected List<Fiscal> call() throws Exception {
                List<Fiscal> lista = servicio.listarFiscales();
                for (Fiscal fiscal : lista) {
                    //Buscamos y cargamos, si tiene, la calle en el objeto fiscal
                    if (fiscal.getDireccion().getCalle() != null) {
                        Calle c = calleServicio.buscarPorId(fiscal.getDireccion().getCalle().getIdCalle());
                        fiscal.getDireccion().setCalle(c);
                        fiscal.getDireccion().setAltura(fiscal.getDireccion().getAltura());
                    }
                    if (fiscal.getDireccion().getTipoPiso() != null) {
                        TipoPiso tp = tipoPisoServicio.buscarPorId(fiscal.getDireccion().getTipoPiso().getIdPiso());
                        fiscal.getDireccion().setTipoPiso(tp);
                    }
                    if (fiscal.getDireccion().getTipoDepartamento() != null) {
                        TipoDepartamento tipoDpto = tipoDeptoServicio.buscarPorId(fiscal.getDireccion().getTipoDepartamento().getIdDepartamento());
                        fiscal.getDireccion().setTipoDepartamento(tipoDpto);
                    }
                    //Buscamos y cargamos en el objeto fiscal el TipoFiscal
                    if (fiscal.getTipoFiscal().getIdTipoFiscal() != null) {
                        TipoFiscal tf = tipoFiscalServicio.buscarPorID(fiscal.getTipoFiscal().getIdTipoFiscal());
                        fiscal.setTipoFiscal(tf);
                    }
                    if (fiscal.getEstablecimientoVotacion() != null) {
                        EstablecimientoResponseDTO dto = estServicio.buscarPorId(fiscal.getEstablecimientoVotacion().getIdEstablecimiento());
                        Establecimiento est = EstablecimientoMapper.aEntidadModelo(dto);
                        fiscal.setEstablecimientoVotacion(est);
                    }
                    if (fiscal.getMesa() != null) {
                        MesaResponseDTO ms = mesaServicio.buscoMesaPorId(fiscal.getMesa().getIdMesa());
                        Mesa mesa = MesaMapper.aEntidadCompleta(ms);
                        fiscal.setMesa(mesa);
                    }
                    if (fiscal.getJornada() != null) {
                        JornadaResponseDTO jdn = jornadaServicio.buscarPorId(fiscal.getJornada().getIdJornada());
                        Jornada jornada = JornadaMapper.aEntidadCompleta(jdn);
                        fiscal.setJornada(jornada);
                    }

                }
                return lista;
            }
        };
        tarea.setOnSucceeded(event -> {
            listaFiscales.setAll(tarea.getValue());
            //Ordenamos la lista de fiscales
            SortedList<Fiscal> listaOrdenada = new SortedList<>(
                    listaFiscales,
                    Comparator.comparing(fiscal -> fiscal.getApellidoFiscal() != null ? fiscal.getApellidoFiscal() : "")
            );
            //Establecemos la lista ordenada para la tabla
            tablaFiscales.setItems(listaOrdenada);
            //Vinculamos el comparador con la tabla
            tablaFiscales.comparatorProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    listaOrdenada.comparatorProperty().bind(tablaFiscales.comparatorProperty());
                }
            });
        });
        tarea.setOnFailed(evento -> mostrarAlerta("Error", "No se pudo recuperar la lista de fiscales " +
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

    private void cargarTiposFiscales() {
        Task<List<TipoFiscal>> tarea = new Task<List<TipoFiscal>>() {
            @Override
            protected List<TipoFiscal> call() throws Exception {
                return tipoFiscalServicio.listarTiposFiscales();
            }
        };

        tarea.setOnSucceeded(event -> elementoTipoFiscal.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarAlerta("Error", "No se ha podido cargar los tipos de fiscales", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private void cargarJornadas() {
        Task<List<Jornada>> tarea = new Task<List<Jornada>>() {
            @Override
            protected List<Jornada> call() throws Exception {
                return jornadaServicio.listarJornadas();
            }
        };
        tarea.setOnSucceeded(evento -> elementoJornada.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarAlerta("Error", "No se pudo recuperar la lista de jornadas", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }


    private void cargarTipoDepartamento() {
        Task<List<TipoDepartamento>> tarea = new Task<List<TipoDepartamento>>() {
            @Override
            protected List<TipoDepartamento> call() throws Exception {
                return tipoDeptoServicio.listarTiposDepartamentos();
            }
        };

        tarea.setOnSucceeded(evento -> elementoTipoDepartamento.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarAlerta("Error", "No se ha podido cargar los departamentos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private void cargarTiposPisos() {
        Task<List<TipoPiso>> tarea = new Task<List<TipoPiso>>() {
            @Override
            protected List<TipoPiso> call() throws Exception {
                return tipoPisoServicio.listarTipoPiso();
            }
        };

        tarea.setOnSucceeded(evento -> elementoTipoPisoFiscal.getItems().setAll(tarea.getValue()));
        tarea.setOnFailed(evento -> mostrarAlerta("Errro", "No se pudieron cargar los pisos", Alert.AlertType.ERROR));
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

    //construimos el combobox para la búsqueda de calles para el elemento de donde vota
    private void cargarEstablecimientoVota() {
        Task<List<Establecimiento>> tareaEst = new Task<List<Establecimiento>>() {
            @Override
            protected List<Establecimiento> call() throws Exception {
                return estServicio.listarEstablecimientos();
            }
        };
        tareaEst.setOnSucceeded(evento -> {
            List<Establecimiento> lista = tareaEst.getValue();
            elementoEstablecimientoVota.getItems().setAll(lista.stream().sorted(ordenarListas(Establecimiento::getNombreEstablecimiento)).toList());
        });
        tareaEst.setOnFailed(event -> mostrarAlerta("Error", "No se pudieron cargar los establecimientos" + tareaEst.getException(), Alert.AlertType.ERROR));
        new Thread(tareaEst).start();
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

    private void habilitarGuardar() {
        boolean camposObligatorios = txtValorId.getText().isBlank() &&
                !txtNombreFiscal.getText().isEmpty() &&
                !txtApellidoFiscal.getText().isEmpty() &&
                !txtEdadFiscal.getText().isEmpty() &&
                !txtCorreoFiscal.getText().isEmpty() &&
                !txtTelefonoFiscal.getText().isEmpty() &&
                elementoTipoFiscal.getValue() != null &&
                elementoEstablecimientoVota.getValue() != null &&
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
                elementoEstablecimientoVota.getValue() != null &&
                !campoBuscarCalle.getText().isEmpty() &&
                !txtAlturaDireccionFiscal.getText().isEmpty();
        btnactualizarFiscal.setDisable(!camposObligatorios);
        if (camposObligatorios) {
            msgFiscalGuardado.setText("");
        }

    }

}
