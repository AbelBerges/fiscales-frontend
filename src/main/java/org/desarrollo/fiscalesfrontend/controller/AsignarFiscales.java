package org.desarrollo.fiscalesfrontend.controller;

import com.sun.source.tree.TryTree;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.desarrollo.fiscalesfrontend.dto.AsignacionMesasRequestDTO;
import org.desarrollo.fiscalesfrontend.mapper.JornadaMapper;
import org.desarrollo.fiscalesfrontend.model.*;
import org.desarrollo.fiscalesfrontend.service.*;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AsignarFiscales {

    @FXML private ComboBox<Establecimiento> comboBoxEstablecimiento;
    @FXML private ComboBox<Fiscal> comboBoxFiscalGeneral;
    @FXML private FlowPane contenedorMesas;
    @FXML private ScrollPane scrollMesas;
    @FXML private HBox contenerFiscalGeneral;
    @FXML private Button btnGuardarFiscales, btnActAsignacionFiscales, btnCancelar, btnGuardarFiscalGeneral;
    @FXML private Label msjexito, valorID, etiquetaId, generales;

    //Declaramos los servicios que vamos a necesitar
    private EstablecimientoServicio servicoEst = new EstablecimientoServicio();
    private MesaServicio mesaServicio = new MesaServicio();
    private FiscalServicio fiscalServicio = new FiscalServicio();
    private TipoFiscalServicio tipoFiscalServicio = new TipoFiscalServicio();
    private JornadaServicio jornadaServicio = new JornadaServicio();

    //Declaramos elementos que vamos a usar en el controlador
    private ObservableList<Establecimiento> listaEst = FXCollections.observableArrayList();
    private ObservableList<Mesa> mesasSeleccionadas = FXCollections.observableArrayList();
    private List<Mesa>  listaMesas = new ArrayList<>();
    private ObservableList<Fiscal> fiscalesSinMesa = FXCollections.observableArrayList();
    private ObservableList<Fiscal> fiscalesJornadaManana = FXCollections.observableArrayList();
    private ObservableList<Fiscal> fiscalesJornadaTarde = FXCollections.observableArrayList();
    private ObservableList<Fiscal> fiscalesJornadaCompleta = FXCollections.observableArrayList();
    private ObservableList<Fiscal> fiscalesGenerales = FXCollections.observableArrayList();
    private ObservableList<Fiscal> fiscalesGeneralesCombo = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        scrollMesas.setFitToWidth(true);
        scrollMesas.setPannable(true);
        contenedorMesas.setHgap(15);
        contenedorMesas.setVgap(15);
        contenedorMesas.setPrefWrapLength(900);

        //Iniciamos el estado del combobox
        comboBoxEstablecimiento.setPromptText("Seleccione un establecimiento");
        cargarEstablecimientos();
        comboBoxFiscalGeneral.setPromptText("Seleccione un Fiscal General");
        comboBoxFiscalGeneral.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Fiscal item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(!vacio || item != null ? item.getNombreFiscal() + ", " + item.getApellidoFiscal() : null);
            }
        });
        comboBoxFiscalGeneral.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fiscal item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(!vacio || item != null ? item.getNombreFiscal() + ", " + item.getApellidoFiscal() : "Seleccione un Fiscal General");
            }
        });
        //cargarComboFiscalesGenerales();

        comboBoxEstablecimiento.valueProperty().addListener((obs, ov,nv) -> {
            cargarMesasPorEstablecimiento(nv.getIdEstablecimiento());
        });
    }

    @FXML
    private void guardarFiscalGeneral() {
        Task<Void> tarea = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Integer idFiscal = comboBoxFiscalGeneral.getValue().getIdFiscal();
                Integer idEst = comboBoxEstablecimiento.getValue().getIdEstablecimiento();
                fiscalServicio.asignoFiscalGeneral(idFiscal, idEst);
                return null;
            }
        };
        tarea.setOnSucceeded(evento -> {
            try {
                Fiscal f = fiscalServicio.buscoPorId(comboBoxFiscalGeneral.getValue().getIdFiscal());
                HBox fila = agregarCeldaFiscalGeneral(f, contenerFiscalGeneral);
                contenerFiscalGeneral.getChildren().add(fila);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            cargarComboFiscalesGenerales();
        });
        tarea.setOnFailed(evento ->
                mostrarMensaje("Error", "No se pudo asignar el fiscal " +
                        tarea.getException().getMessage(), Alert.AlertType.ERROR));
        new Thread(tarea).start();

    }

    private void cargarComboFiscalesGenerales() {
        Task<List<Fiscal>> tarea = new Task<List<Fiscal>>() {
            @Override
            protected List<Fiscal> call() throws Exception {
                String tipo = "General";
                TipoFiscal elTipo = tipoFiscalServicio.buscarFiscalPorNombre(tipo);
                return fiscalServicio.listarTipoFiscalEstablecimientoNull(elTipo.getIdTipoFiscal());
            }
        };
        tarea.setOnSucceeded(evento -> {
            List<Fiscal> lista = new ArrayList<>(tarea.getValue());
            lista.sort(getComparatorAlfabetico());
            fiscalesGeneralesCombo = FXCollections.observableArrayList(lista);
            comboBoxFiscalGeneral.setItems(fiscalesGeneralesCombo);
        });
        tarea.setOnFailed( evento ->
                mostrarMensaje("Error", "No se pudo recuperar la lista de Fiscales con un tipo determinado", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }


    private void cargarMesasPorEstablecimiento(Integer elId) {
        limpiarFormulario();
        try {
            contenerFiscalGeneral.setPadding(new Insets(0, 5, 0, 5));
            Task<List<Fiscal>> tarea = new Task<List<Fiscal>>() {
                @Override
                protected List<Fiscal> call() throws Exception {
                    return fiscalServicio.listarPorEstablecimientoAsignado(elId);
                }
            };
            tarea.setOnSucceeded(evento -> {
                cargarComboFiscalesGenerales();
                if (!tarea.getValue().isEmpty() || tarea.getValue() != null) {
                    List<Fiscal> existen = new ArrayList<>(tarea.getValue());
                    existen.sort(getComparatorAlfabetico());
                    fiscalesGenerales = FXCollections.observableArrayList(existen);
                    for (Fiscal f : existen) {
                        HBox fila = agregarCeldaFiscalGeneral(f, contenerFiscalGeneral);
                        contenerFiscalGeneral.getChildren().add(fila);
                    }
                }
            });
            tarea.setOnFailed(evento ->
                    mostrarMensaje("Error", "no se pudo cargar los fiscales generales asignados", Alert.AlertType.ERROR));
            new Thread(tarea).start();

            Task<Void> armarMesas = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    //Armamos los objetos por mesa
                    TipoFiscal tf = tipoFiscalServicio.buscarFiscalPorNombre("Mesa");
                    construirListasObservables(tf);
                    AsignacionMesasRequestDTO dto = mesaServicio.mesasPorEstablecimiento(elId);
                    Platform.runLater(() -> {
                        for (Integer i : dto.numerosMesa()) {
                            try {
                                Mesa temp = mesaServicio.buscoPorNumeroMesa(i);
                                mesasSeleccionadas.add(temp);
                                agregarFilaMesa(temp);
                            } catch (IOException | InterruptedException e) {
                                mostrarMensaje("Error", "No se pudo recuperar la mesa", Alert.AlertType.ERROR);
                            }

                        }
                    });
                    return null;
                }
            };
            //armarMesas.setOnSucceeded(evento -> System.out.println("se cargaron las mesas"));
            armarMesas.setOnFailed(evento -> {
                Throwable ex = armarMesas.getException();
                ex.printStackTrace();
                mostrarMensaje("Error", "No se pudo cargar las mesas para el tipo de fiscal" + ex.getMessage(), Alert.AlertType.ERROR);
            });
            new Thread(armarMesas).start();

        } catch (Exception e) {
            mostrarMensaje("Error", "No se pudo cagar la mesa en el panel " + e.getMessage(), Alert.AlertType.ERROR);
        }

    }


    private String normalizar(String texto) {
        if (texto == null) return "";
        String nfd = java.text.Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{M}", ""); // elimina diacríticos (acentos)
    }

    private void construirListasObservables(TipoFiscal tf) {
        try {
            Jornada completa = jornadaServicio.buscarJornadaPorTipo("COMPLETA");
            Jornada manana = jornadaServicio.buscarJornadaPorTipo("MAÑANA");
            Jornada tarde = jornadaServicio.buscarJornadaPorTipo("TARDE");
            List<Fiscal> listaBaseJornadaCompleta = new ArrayList<>(fiscalServicio.listaFiscalesTipoFiscalJornadaSinMesa(tf.getIdTipoFiscal(), completa.getIdJornada()));
            List<Fiscal> listaBaseJornadaManana = new ArrayList<>(fiscalServicio.listaFiscalesTipoFiscalJornadaSinMesa(tf.getIdTipoFiscal(), manana.getIdJornada()));
            List<Fiscal> listaBaseJornadaTarde = new ArrayList<>(fiscalServicio.listaFiscalesTipoFiscalJornadaSinMesa(tf.getIdTipoFiscal(), tarde.getIdJornada()));
            //Ordenamos con un comparador para que sea alfabético
            Comparator<Fiscal> porApellido = getComparatorAlfabetico();
            listaBaseJornadaCompleta.sort(porApellido);
            listaBaseJornadaManana.sort(porApellido);
            listaBaseJornadaTarde.sort(porApellido);
            fiscalesJornadaCompleta = FXCollections.observableArrayList(listaBaseJornadaCompleta);
            fiscalesJornadaManana = FXCollections.observableArrayList(listaBaseJornadaManana);
            fiscalesJornadaTarde = FXCollections.observableArrayList(listaBaseJornadaTarde);
        } catch (IOException | InterruptedException e) {
            Throwable ex = e;
            mostrarMensaje("Error", "Error al construir las ObservableList" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private Comparator<Fiscal> getComparatorAlfabetico() {
        return Comparator.comparing(
                (Fiscal f) -> normalizar(f.getApellidoFiscal()), String.CASE_INSENSITIVE_ORDER
        ).thenComparing((Fiscal f) -> normalizar(f.getNombreFiscal()), String.CASE_INSENSITIVE_ORDER);
    }

    private void actualizarEstadoMesa(VBox contenedorMesa, VBox contenedorFiscales, HBox filaCompleta, HBox filaManana, HBox filaTarde) {
        boolean manana = false;
        boolean tarde = false;
        boolean completa = false;

        for (Node node : contenedorFiscales.getChildren()) {
            if (!(node instanceof HBox)) continue;
            HBox fila = (HBox) node;
            Object ud = fila.getUserData();
            if (ud instanceof Fiscal f) {
                try {
                    Jornada j = JornadaMapper.aEntidadCompleta(jornadaServicio.buscarPorId(f.getJornada().getIdJornada()));
                    if (j == null || j.getTipoJornada() == null) {
                        String tipo = j.getTipoJornada().toUpperCase(Locale.ROOT).trim();
                        switch (tipo) {
                            case "MAÑANA" -> manana = true;
                            case "TARDE" -> tarde = true;
                            case "COMPLETA" -> completa = true;
                        }
                    } else {
                        // Fallback: si no hay userData, intentamos leer el label de jornada en posición conocida
                        if (fila.getChildren().size() > 1 && fila.getChildren().get(1) instanceof Label lab) {
                            String texto = lab.getText();
                            if (texto != null) {
                                String t = texto.toUpperCase(Locale.ROOT);
                                if (t.contains("MAÑANA") || t.contains("M")) manana = true;
                                if (t.contains("TARDE") || t.contains("T")) tarde = true;
                                if (t.contains("COMPLETA") || t.contains("C")) completa = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    mostrarMensaje("Error", "No se ha podido recuperar la jornada", Alert.AlertType.ERROR);
                }
            }
        }
        // Determinar estado (teniendo en cuenta tu regla: no pueden existir 2 del mismo turno)
        // Si hay jornada completa -> completa
        boolean esCompleta = completa || (manana && tarde);
        boolean esVacia = !manana && !tarde && !completa;
        boolean esIncompleta = !esVacia && !esCompleta;

        //Limpiar estilos previos del contenedorMesa
        try {
            contenedorMesa.getStyleClass().removeAll("mesa-completa", "mesa-incompleta", "mesa-vacia");
        } catch (Exception ignored) {}
        //Limpiar de estilo el inline previo
        contenedorMesa.setStyle("");
        //Aplicar estilo y bloqueos de forma consistente
        if (esVacia) {
            animarCambio(contenedorMesa);
            contenedorMesa.setStyle(
                    "-fx-background-color: rgba(167, 167, 167, 0.478);" +
                    "-fx-border-color: rgb(189, 189, 189);" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;"
            );
            //flashCompleto(contenedorMesa);
            filaCompleta.setDisable(false);
            filaManana.setDisable(false);
            filaTarde.setDisable(false);
        } else if (esCompleta) {
            // estilo verde
            animarCambio(contenedorMesa);
            contenedorMesa.setStyle(
                    "-fx-background-color: rgb(152, 168, 134, 0.356);" +
                    "-fx-border-color: rgba(94, 168, 94, 0.781);" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;"
            );
            //flashCompleto(contenedorMesa);
            contenedorMesa.setEffect(new DropShadow(15, Color.rgb(120,170, 120,0.25)));
            filaCompleta.setDisable(true);
            filaManana.setDisable(true);
            filaTarde.setDisable(true);
        } else { //incompleta
            animarCambio(contenedorMesa);
            contenedorMesa.setStyle(
                    "-fx-border-color: #ecb669;" +
                    "-fx-border-width: 1;" +
                    "-fx-background-color: rgba(196, 166, 157, 0.356);" +
                    "-fx-border-radius: 6;" +
                    "-fx-background-radius: 6;"
            );
            //flashCompleto(contenedorMesa);
            contenedorMesa.setEffect(new DropShadow(15, Color.rgb(200, 140,40, 0.25)));
            filaCompleta.setDisable(false);
            filaManana.setDisable(false);
            filaTarde.setDisable(false);
            if (manana){
                filaManana.setDisable(true);
                filaCompleta.setDisable(true);
            }
            if (tarde) {
                filaCompleta.setDisable(true);
                filaTarde.setDisable(true);
            }

        }
    }

    private void animarCambio(Node nodo) {
        FadeTransition ft = new FadeTransition(Duration.millis(350), nodo);
        ft.setFromValue(0.3);
        ft.setToValue(1);
        ft.play();
    }

    private HBox agregarCeldaFiscalGeneral(Fiscal fiscal, HBox contenerFiscalGeneral) {
        HBox contenedor = new HBox(5);
        contenedor.setAlignment(Pos.CENTER_LEFT);
        contenedor.setPadding(new Insets(5));
        //Aplicamos un poco de diseño a contenedor
        contenedor.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #cccccc;" +
                "-fx-border-radius: 6;" +
                "-fx-border-width: 1;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0.1, 0, 1);"
        );

        //Efecto hover manual
        contenedor.setOnMouseEntered(e -> {
            contenedor.setStyle(
                    "-fx-background-color: rgb(223, 223, 223);" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #d0d0d0;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0.2, 0, 2);"
            );
        });
        //Cuando sale del hover
        contenedor.setOnMouseExited(e -> {
            contenedor.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #e0e0e0;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0.1, 0, 1);"
            );
        });

        Label nombre = new Label(fiscal.getApellidoFiscal());
        nombre.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #333333;"
        );
        nombre.setMinWidth(80);
        Button btnQuitarfiscal = new Button("\uD83D\uDDD1");
        btnQuitarfiscal.setStyle(
                "-fx-background-color: rgb(255, 134, 134);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5 10;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 12px;"
        );
        // Hover del botón
        btnQuitarfiscal.setOnMouseEntered(e ->
                btnQuitarfiscal.setStyle(
                        "-fx-background-color: #e64949;" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 12px;"
                )
        );

        btnQuitarfiscal.setOnMouseExited(e ->
                btnQuitarfiscal.setStyle(
                        "-fx-background-color: rgb(255, 134, 134);" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 5 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 12px;"
                )
        );

        btnQuitarfiscal.setOnAction(e -> {
            boolean exito = liberoFiscalGeneral(fiscal.getIdFiscal());
            if (!exito) return;
            contenerFiscalGeneral.getChildren().remove(contenedor);
            fiscalesGeneralesCombo.add(fiscal);
        });
        contenedor.getChildren().addAll(nombre, btnQuitarfiscal);
        return contenedor;
    }

    private boolean liberoFiscalGeneral(Integer idFiscal) {
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                fiscalServicio.desasignarFiscalGeneral(idFiscal);
                return true;
            }
        };
        tarea.setOnSucceeded(evento -> {
            System.out.println("Se ha liberado el fiscal general");
        });
        tarea.setOnFailed(evento -> {
            Throwable ex = tarea.getException();
            ex.printStackTrace();
            mostrarMensaje("Error", "No se ha podido liberar el fiscal general " + ex.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
        return true;
    }

    private void agregarFilaMesa(Mesa mesa) {
        //1) Contenedor principal de la mesa
        VBox contenedorMesa = new VBox(10);
        contenedorMesa.setAlignment(Pos.TOP_LEFT);
        contenedorMesa.setPadding(new Insets(10));
        contenedorMesa.setPrefWidth(380);

        Label titulo = new Label("Mesa: " + mesa.getNumeroMesa());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        //Declaramos los comboBox
        ComboBox<Fiscal> comboCompleta = new ComboBox<>();
        comboCompleta.setPromptText("Seleccionar");
        construirComboBox(comboCompleta, "Seleccionar");
        ComboBox<Fiscal> comboManana = new ComboBox<>();
        comboManana.setPromptText("Seleccionar");
        construirComboBox(comboManana, "Seleccionar");
        ComboBox<Fiscal> comboTarde = new ComboBox<>();
        comboTarde.setPromptText("Seleccionar");
        construirComboBox(comboTarde, "Seleccionar");

        //Declaramos los botones para cada comboBox
        Button btnAsignarCompleto = new Button("Asignar");
        Button btnAsignarManana = new Button("Asignar");
        Button btnAsignarTarde = new Button("Asignar");

        //Declaramos los HBox para las filas
        HBox filaCompleta = filaAsignador("Completa", comboCompleta, btnAsignarCompleto);
        HBox filaManana = filaAsignador("Mañana", comboManana, btnAsignarManana);
        HBox filaTarde = filaAsignador("Tarde", comboTarde, btnAsignarTarde);


        Separator sepTop = new Separator();
        //2) Contenedor donde van los fiscales ya asignados
        VBox contenedorFiscales = new VBox(5);
        contenedorFiscales.setPadding(new Insets(5,0,5,0));
        try {
            List<Fiscal> existentes = new ArrayList<>(fiscalServicio.buscoFiscalPorIdMesa(mesa.getIdMesa()));
            //Creamos un ordenador para los fiscales una vez seleccionados
            Map<String, Integer> orden = Map.of(
                    "MAÑANA", 1,
                    "TARDE", 2,
                    "COMPLETA", 3
            );
            existentes.sort((f1, f2) -> {
                try {
                    Jornada j1 = JornadaMapper.aEntidadCompleta(
                            jornadaServicio.buscarPorId(f1.getJornada().getIdJornada()));
                    Jornada j2 = JornadaMapper.aEntidadCompleta(
                            jornadaServicio.buscarPorId(f2.getJornada().getIdJornada()));
                    Integer o1 = orden.get(j1.getTipoJornada());
                    Integer o2 = orden.get(j2.getTipoJornada());
                    if (o1 == null) o1 = 98;
                    if (o2 == null) o2 = 99;
                    return o1 - o2;
                } catch (Exception e) {
                    return 0;
                }
            });
            if (!existentes.isEmpty()) {
                for (Fiscal f: existentes) {
                    Jornada jornada = JornadaMapper.aEntidadCompleta(jornadaServicio.buscarPorId(f.getJornada().getIdJornada()));
                    HBox fila = crearFilaFiscal(f, mesa, contenedorFiscales, jornada, filaCompleta, filaManana, filaTarde, contenedorMesa);
                    contenedorFiscales.getChildren().add(fila);
                    actualizarEstadoMesa(contenedorMesa, contenedorFiscales, filaCompleta, filaManana, filaTarde);
                }
            } else {
                actualizarEstadoMesa(contenedorMesa, contenedorFiscales, filaCompleta, filaManana, filaTarde);
            }

        } catch (IOException | InterruptedException e) {
            mostrarMensaje("Error", "No se pudo recuperar la lista de mensajes de fiscales asignados", Alert.AlertType.ERROR);
        }

        Separator sepMid = new Separator();
        //3) Contenedor para las 3 filas de asignación
        VBox contenedorAsignacion = new VBox();
        //Agregamos el ordenamiento antes de cargar las listas en los comboboxs
        SortedList<Fiscal> completaOrdenada = new SortedList<>(fiscalesJornadaCompleta, getComparatorAlfabetico());
        SortedList<Fiscal> mananaOrdenada = new SortedList<>(fiscalesJornadaManana, getComparatorAlfabetico());
        SortedList<Fiscal> tardeOrdenada = new SortedList<>(fiscalesJornadaTarde, getComparatorAlfabetico());

        //Cargamos las ObservableList en los combos
        comboCompleta.setItems(completaOrdenada);
        comboManana.setItems(mananaOrdenada);
        comboTarde.setItems(tardeOrdenada);

        contenedorAsignacion.getChildren().addAll(filaCompleta, filaManana, filaTarde);

        Separator sepBottom = new Separator();
        //4) Eventos de asignación
        btnAsignarCompleto.setOnAction(ev ->
                asignarFiscal(mesa, comboCompleta, "COMPLETA", contenedorFiscales, filaCompleta, filaManana, filaTarde, fiscalesJornadaCompleta, contenedorMesa));
        btnAsignarManana.setOnAction(ev ->
                asignarFiscal(mesa, comboManana, "MAÑANA", contenedorFiscales, filaCompleta, filaManana, filaTarde, fiscalesJornadaManana, contenedorMesa));
        btnAsignarTarde.setOnAction(ev ->
                asignarFiscal(mesa, comboTarde, "TARDE", contenedorFiscales, filaCompleta, filaManana, filaTarde, fiscalesJornadaTarde, contenedorMesa));

        //5) Ensamblado final
        contenedorMesa.getChildren().addAll(titulo, sepTop, contenedorFiscales, sepMid, contenedorAsignacion, sepBottom);
        contenedorMesas.getChildren().add(contenedorMesa);
    }

    private HBox filaAsignador(String titulo, ComboBox<Fiscal> combo, Button boton) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.TOP_LEFT);
        Label lbl = new Label(titulo + ":");
        lbl.setPrefWidth(80);
        combo.setMinWidth(180);
        combo.setMaxWidth(180);
        boton.setPrefWidth(80);
        fila.getChildren().addAll(lbl, combo, boton);
        return fila;
    }


    private void asignarFiscal(Mesa mesa, ComboBox<Fiscal> combo, String jornada, VBox contendedorFiscales,
    HBox filaCompleta, HBox filaManana, HBox filaTarde, ObservableList<Fiscal> lista, VBox contenedorMesa) {
        Fiscal seleccionado = combo.getValue();
        if (seleccionado == null) return;
        try {
            fiscalServicio.asingarFiscalAUnaMesa(seleccionado.getIdFiscal(), mesa.getIdMesa());
            Jornada jor = jornadaServicio.buscarJornadaPorTipo(jornada);
            //Creamos el nodo visual y asociamos al fiscal
            HBox fila = crearFilaFiscal(seleccionado, mesa, contendedorFiscales, jor, filaCompleta, filaManana, filaTarde, contenedorMesa);
            fila.setUserData(seleccionado);
            contendedorFiscales.getChildren().add(fila);
            //Quitar de la ObservableList
            quitarFiscalDeLaLista(seleccionado, lista);
            //Aplicamos reglas según jornada

            actualizarEstadoMesa(contenedorMesa, contendedorFiscales, filaCompleta, filaManana, filaTarde);
        } catch (IOException | InterruptedException e) {
            mostrarMensaje("Error", "No se pudo asignar el fiscal", Alert.AlertType.ERROR);
        }
    }

    private void devolverFiscalASuListaCorrespondiente(Fiscal f, Jornada jornada) {
        switch (jornada.getTipoJornada()) {
            case "COMPLETA": {
                fiscalesJornadaCompleta.add(f);
                break;
            }
            case "MAÑANA": {
                fiscalesJornadaManana.add(f);
                break;
            }
            case "TARDE": {
                fiscalesJornadaTarde.add(f);
            }
        }
    }

    private void quitarFiscalDeLaLista(Fiscal seleccionado, ObservableList<Fiscal> lista) {
        lista.remove(seleccionado);
    }


    private HBox crearFilaFiscal(Fiscal fiscal, Mesa mesa, VBox contendorFiscales, Jornada jornada,
                                 HBox filaCompeta, HBox filaManana, HBox filaTarde, VBox contenedorMesa) {
        HBox fila = new HBox(10);
        fila.setAlignment(Pos.TOP_LEFT);
        Label lblFiscal = new Label(fiscal.getApellidoFiscal());
        lblFiscal.setMinWidth(120);
        Label lblJornada = crearLabelJornada(jornada);
        Button btnQuitar = new Button("\uD83D\uDDD1");
        btnQuitar.setStyle(
                "-fx-background-color: rgb(255, 134, 134);" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 8;" +
                        "-fx-font-size: 12px;"
        );

        // Hover del botón
        btnQuitar.setOnMouseEntered(e ->
                btnQuitar.setStyle(
                        "-fx-background-color: rgb(255, 43, 43);" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 3 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 12px;"
                )
        );

        btnQuitar.setOnMouseExited(e ->
                btnQuitar.setStyle(
                        "-fx-background-color: rgb(255, 134, 134);" +
                                "-fx-text-fill: white;" +
                                "-fx-padding: 3 10;" +
                                "-fx-background-radius: 8;" +
                                "-fx-font-size: 12px;"
                )
        );
        fila.setUserData(fiscal);

        btnQuitar.setOnAction(e -> {
            boolean exito = desasignarFiscal(fiscal.getIdFiscal());
            if (!exito) return;
            contendorFiscales.getChildren().remove(fila);
            devolverFiscalASuListaCorrespondiente(fiscal, jornada);;
            //VBox contenedorMesa, VBox contenedorFiscales, HBox filaCompleta, HBox filaManana, HBox filaTarde
            actualizarEstadoMesa(contenedorMesa, contendorFiscales, filaCompeta, filaManana, filaTarde);
        });
        fila.getChildren().addAll(lblFiscal, lblJornada, btnQuitar);
        return fila;
    }

    private Label crearLabelJornada(Jornada j) {
        Label l = new Label();
        switch (j.getTipoJornada()) {
            case "MAÑANA": {
                l.setText("M");
                l.setStyle("-fx-background-color:#d0e7ff; -fx-padding:2 5; -fx-background-radius:3; -fx-font-size:11px;");
                break;
            }
            case "TARDE": {
                l.setText("T");
                l.setStyle("-fx-background-color:#ffe1c4; -fx-padding:2 5; -fx-background-radius:3; -fx-font-size:11px;");
                break;
            }
            case "COMPLETA": {
                l.setText("C");
                l.setStyle("-fx-background-color:#d4f8d4; -fx-padding:2 5; -fx-background-radius:3; -fx-font-size:11px;");
                break;
            }
        }
        return l;
    }

    private boolean desasignarFiscal(Integer idFiscal) {
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                fiscalServicio.desasignarUnFiscalAUnaMesa(idFiscal);
                return true;
            }
        };
        tarea.setOnSucceeded(evento -> System.out.println("Se ha desasigando el fiscal"));
        tarea.setOnFailed(evento ->{
            Throwable ex = tarea.getException();
            ex.printStackTrace();
            mostrarMensaje("Error", "No se pudo desasignar el fiscal a la mesa" + ex.getMessage(), Alert.AlertType.ERROR);
        });
        new Thread(tarea).start();
        return true;
    }

    private Task<Boolean> asignarFiscalMesa(Fiscal fiscal, Mesa mesa) {
        Task<Boolean> tarea = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                fiscalServicio.asingarFiscalAUnaMesa(fiscal.getIdFiscal(), mesa.getIdMesa());
                return true;
            }
        };
        tarea.setOnSucceeded(event -> System.out.println("se ha asignado el fiscal"));
        tarea.setOnFailed(evento -> {
            Throwable ex = tarea.getException();
            ex.printStackTrace();
            Platform.runLater(() -> {
                mostrarMensaje("Error", "No se pudo asignar el fiscal a la mesa" + ex.getMessage(), Alert.AlertType.ERROR);
            });
        });
        new Thread(tarea).start();
        return tarea;
    }

    private void construirComboBox(ComboBox<Fiscal> combo, String msj) {
        combo.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(Fiscal item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(!vacio || item != null ? item.getApellidoFiscal() + ", " + item.getNombreFiscal() : null);
            }
        });

        combo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Fiscal item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(!vacio || item != null ? item.getApellidoFiscal() + ", " + item.getNombreFiscal() : msj);
            }
        });

    }

    private void cargarEstablecimientos() {
        Task<List<Establecimiento>> tarea = new Task<List<Establecimiento>>() {
            @Override
            protected List<Establecimiento> call() throws Exception {
                return servicoEst.listarEstablecimientos();
            }
        };
        tarea.setOnSucceeded(evento -> {
            List<Establecimiento> lista = tarea.getValue();
            comboBoxEstablecimiento.getItems().setAll(lista.stream().sorted(ordenarLista(Establecimiento::getNombreEstablecimiento)).toList());
        });
        tarea.setOnFailed(evnto -> mostrarMensaje("Error", "No se pudo recuperar la lista de Establecimientos", Alert.AlertType.ERROR));
        new Thread(tarea).start();
    }

    private <T> Comparator<T> ordenarLista(Function<T, String> mapper) {
        return Comparator.comparing(t-> normalizar(mapper.apply(t)), String.CASE_INSENSITIVE_ORDER);
    }



    private void mostrarMensaje(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void limpiarFormulario() {
        mesasSeleccionadas.clear();
        //generales.setText("");
        contenerFiscalGeneral.getChildren().clear();
        //comboBoxFiscalGeneral.getSelectionModel().clearSelection();
        contenedorMesas.getChildren().clear();
    }
}
