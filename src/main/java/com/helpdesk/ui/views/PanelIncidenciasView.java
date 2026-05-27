package com.helpdesk.ui.views;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Incidencia.Categoria;
import com.helpdesk.model.Incidencia.Estado;
import com.helpdesk.model.Incidencia.Prioridad;
import com.helpdesk.model.Tecnico;
import com.helpdesk.model.Usuario;
import com.helpdesk.service.ExportService;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;
import com.helpdesk.ui.components.CambioEstadoDialog;
import com.helpdesk.ui.components.IncidenciaFormDialog;
import com.helpdesk.util.UIHelper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class PanelIncidenciasView extends BorderPane {

    private final IncidenciaService service;
    private final Runnable onNavigateDetail;
    private final ExportService exportService;

    private final TableView<Incidencia> tabla = new TableView<>();
    private final ObservableList<Incidencia> datos = FXCollections.observableArrayList();

    // Filtros
    private final ComboBox<String> cbFiltroCategoria = new ComboBox<>();
    private final ComboBox<String> cbFiltroPrioridad = new ComboBox<>();
    private final ComboBox<String> cbFiltroEstado = new ComboBox<>();
    private final ComboBox<String> cbFiltroTecnico = new ComboBox<>();
    private final DatePicker dpDesde = new DatePicker();
    private final DatePicker dpHasta = new DatePicker();
    private final TextField tfBusqueda = new TextField();

    private DetalleIncidenciaView detalleView;

    public PanelIncidenciasView(IncidenciaService service, ExportService exportService, Runnable onNavigateDetail) {
        this.service = service;
        this.exportService = exportService;
        this.onNavigateDetail = onNavigateDetail;
        

        construirUI();
        cargarDatos();
    }

    public void setDetalleView(DetalleIncidenciaView dv) {
        this.detalleView = dv;
    }

    private void construirUI() {

        // ── Top bar ───────────────────────────────────────────────
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(16, 20, 12, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("topbar");

        Label title = new Label("Incidencias activas");
        title.getStyleClass().add("topbar-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNueva = new Button("＋  Nueva incidencia");
        btnNueva.getStyleClass().add("btn-primary");
        btnNueva.setOnAction(e -> abrirFormNueva());

        topBar.getChildren().addAll(title, spacer, btnNueva);
        setTop(topBar);

        // ── Filtros ───────────────────────────────────────────────
        VBox filtros = new VBox(8);
        filtros.setPadding(new Insets(10, 20, 10, 20));
        filtros.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #D5DEFD; -fx-border-width: 0 0 1 0;");

        tfBusqueda.setPromptText("🔍  Buscar…");
        tfBusqueda.getStyleClass().add("form-field");
        tfBusqueda.setPrefWidth(220);
        tfBusqueda.textProperty().addListener((o, v, n) -> aplicarFiltros());

        setupCombo(cbFiltroCategoria, "Categoría", getCategoriasOpts());
        setupCombo(cbFiltroPrioridad, "Prioridad", getPrioridadesOpts());
        setupCombo(cbFiltroEstado, "Estado", getEstadosOpts());
        setupCombo(cbFiltroTecnico, "Técnico", getTecnicosOpts());

        // Anchos para que se vean los nombres completos
        cbFiltroCategoria.setPrefWidth(160);
        cbFiltroPrioridad.setPrefWidth(160);
        cbFiltroEstado.setPrefWidth(160);
        cbFiltroTecnico.setPrefWidth(160);

        dpDesde.setPromptText("Desde");
        dpDesde.getStyleClass().add("form-field");
        dpDesde.setPrefWidth(140);

        dpHasta.setPromptText("Hasta");
        dpHasta.getStyleClass().add("form-field");
        dpHasta.setPrefWidth(140);

        dpDesde.valueProperty().addListener((o, v, n) -> aplicarFiltros());
        dpHasta.valueProperty().addListener((o, v, n) -> aplicarFiltros());

        Button btnLimpiar = new Button("✕  Limpiar");
        btnLimpiar.getStyleClass().add("btn-secondary");
        btnLimpiar.setOnAction(e -> limpiarFiltros());

        // Primera fila de filtros
        HBox fila1 = new HBox(12);
        fila1.setAlignment(Pos.CENTER_LEFT);
        fila1.getChildren().addAll(tfBusqueda, cbFiltroCategoria, cbFiltroPrioridad, cbFiltroEstado);

        // Segunda fila de filtros
        HBox fila2 = new HBox(12);
        fila2.setAlignment(Pos.CENTER_LEFT);
        fila2.getChildren().addAll(cbFiltroTecnico, dpDesde, dpHasta, btnLimpiar);

        filtros.getChildren().addAll(fila1, fila2);

        // ── Tabla ───────────────────────────────────────────────
        construirTabla();
        tabla.setItems(datos);

        tabla.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2 && tabla.getSelectionModel().getSelectedItem() != null) {
                abrirDetalle(tabla.getSelectionModel().getSelectedItem());
            }
        });

        VBox center = new VBox(filtros, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        setCenter(center);
    }

    private void construirTabla() {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getStyleClass().add("table-view");

        TableColumn<Incidencia, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId().substring(0, 8)));
        colId.setPrefWidth(80);

        TableColumn<Incidencia, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colTitulo.setPrefWidth(220);

        TableColumn<Incidencia, Prioridad> colPrio = new TableColumn<>("Prioridad");
        colPrio.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colPrio.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Prioridad p, boolean empty) {
                super.updateItem(p, empty);
                setGraphic(empty || p == null ? null : UIHelper.badgePrioridadBox(p));
                setText(null);
            }
        });

        TableColumn<Incidencia, Estado> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Estado e, boolean empty) {
                super.updateItem(e, empty);
                setGraphic(empty || e == null ? null : UIHelper.badgeEstadoBox(e));
                setText(null);
            }
        });

        TableColumn<Incidencia, String> colCat = new TableColumn<>("Categoría");
        colCat.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCategoria() == null ? "" : c.getValue().getCategoria().name()
        ));

        TableColumn<Incidencia, String> colTec = new TableColumn<>("Técnico");
        colTec.setCellValueFactory(c -> {
            String tid = c.getValue().getTecnicoAsignadoId();
            if (tid == null) return new SimpleStringProperty("—");

            return new SimpleStringProperty(
                    service.buscarTecnicoPorId(tid).map(Tecnico::getNombreCompleto).orElse("—")
            );
        });

        TableColumn<Incidencia, String> colFecha = new TableColumn<>("Apertura");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                UIHelper.fmt(c.getValue().getFechaApertura())
        ));

        TableColumn<Incidencia, String> colAcciones = new TableColumn<>("");
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnVer = new Button("👁");
            private final Button btnEditar = new Button("✏");
            private final Button btnEstado = new Button("⇄");
            private final Button btnElim = new Button("🗑");

            private final HBox box = new HBox(4, btnVer, btnEditar, btnEstado, btnElim);

            {
                btnVer.getStyleClass().add("btn-icon");
                btnEditar.getStyleClass().add("btn-icon");
                btnEstado.getStyleClass().add("btn-icon");
                btnElim.getStyleClass().add("btn-icon");

                box.setAlignment(Pos.CENTER);

                btnVer.setOnAction(e -> abrirDetalle(getTableView().getItems().get(getIndex())));
                btnEditar.setOnAction(e -> abrirFormEditar(getTableView().getItems().get(getIndex())));
                btnEstado.setOnAction(e -> abrirCambioEstado(getTableView().getItems().get(getIndex())));
                btnElim.setOnAction(e -> eliminarIncidencia(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                var rol = SessionManager.getInstance().getUsuario().getRol();

                if (rol == Usuario.Rol.USUARIO_FINAL) {
                    // Usuario final NO puede cambiar estado
                    btnEstado.setVisible(false);
                    btnEstado.setManaged(false);

                    // Usuario final SÍ puede eliminar
                    btnElim.setVisible(true);
                    btnElim.setManaged(true);
                } else {
                    // Técnico ve todo
                    btnEstado.setVisible(true);
                    btnEstado.setManaged(true);
                    btnElim.setVisible(true);
                    btnElim.setManaged(true);
                }

                setGraphic(box);
                setText(null);
            }
        });

        tabla.getColumns().addAll(colId, colTitulo, colPrio, colEstado, colCat, colTec, colFecha, colAcciones);

        tabla.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Incidencia inc, boolean empty) {
                super.updateItem(inc, empty);
                getStyleClass().remove("alerta-critica-row");
                if (!empty && inc != null && inc.esAlertaCritica()) {
                    getStyleClass().add("alerta-critica-row");
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    // ACCIONES
    // ─────────────────────────────────────────────────────────────

    private void abrirFormNueva() {
        new IncidenciaFormDialog(service, null).showAndWait().ifPresent(inc -> {
            try {
                service.crearIncidencia(inc);
                cargarDatos();
            } catch (Exception ex) {
                UIHelper.alertError("Error", ex.getMessage());
            }
        });
    }

    private void abrirFormEditar(Incidencia inc) {
        new IncidenciaFormDialog(service, inc).showAndWait().ifPresent(updated -> {
            try {
                service.actualizarIncidencia(updated);
                cargarDatos();
            } catch (Exception ex) {
                UIHelper.alertError("Error", ex.getMessage());
            }
        });
    }

    private void abrirCambioEstado(Incidencia inc) {
        Set<Estado> permitidos = switch (inc.getEstado()) {
            case ABIERTA -> Set.of(Estado.EN_CURSO, Estado.EN_ESPERA);
            case EN_CURSO -> Set.of(Estado.EN_ESPERA, Estado.RESUELTA);
            case EN_ESPERA -> Set.of(Estado.EN_CURSO, Estado.RESUELTA);
            case RESUELTA -> Set.of(Estado.CERRADA);
            case CERRADA -> Set.of();
        };

        if (permitidos.isEmpty()) {
            UIHelper.alertInfo("Info", "Esta incidencia ya está cerrada.");
            return;
        }

        new CambioEstadoDialog(inc.getEstado(), permitidos).showAndWait().ifPresent(r -> {
            try {
                service.cambiarEstado(inc.getId(), r.nuevoEstado(), r.solucion());
                cargarDatos();
            } catch (Exception ex) {
                UIHelper.alertError("Error", ex.getMessage());
            }
        });
    }

    private void abrirDetalle(Incidencia inc) {
        if (detalleView != null) {
            detalleView.cargar(inc);
            if (onNavigateDetail != null) onNavigateDetail.run();
        }
    }

    private void eliminarIncidencia(Incidencia inc) {
        if (UIHelper.confirm("Eliminar incidencia",
                "¿Seguro que deseas eliminar la incidencia «" + inc.getTitulo() + "»? Esta acción no se puede deshacer.")) {
            try {
                service.eliminarIncidencia(inc.getId());
                cargarDatos();
            } catch (Exception ex) {
                UIHelper.alertError("Error", ex.getMessage());
            }
        }
    }

    private void exportarMes() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar CSV");

        LocalDate hoy = LocalDate.now();
        String nombreArchivo = String.format("incidencias_%d_%02d.csv", hoy.getYear(), hoy.getMonthValue());
        fc.setInitialFileName(nombreArchivo);

        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File f = fc.showSaveDialog(getScene() != null ? getScene().getWindow() : null);
        if (f == null) return;

        try {
            exportService.exportarMesActualCSV(f.getAbsolutePath());
            UIHelper.alertInfo("Exportación completa", "Archivo guardado en:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            UIHelper.alertError("Error al exportar CSV", ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // FILTROS
    // ─────────────────────────────────────────────────────────────

    private void aplicarFiltros() {
        String busq = tfBusqueda.getText() == null ? "" : tfBusqueda.getText().toLowerCase();
        String cat = cbFiltroCategoria.getValue();
        String prio = cbFiltroPrioridad.getValue();
        String est = cbFiltroEstado.getValue();
        String tec = cbFiltroTecnico.getValue();

        LocalDateTime desde = dpDesde.getValue() != null ? dpDesde.getValue().atStartOfDay() : null;
        LocalDateTime hasta = dpHasta.getValue() != null ? dpHasta.getValue().plusDays(1).atStartOfDay() : null;

        List<Incidencia> origen = service.listarIncidencias();
        List<Incidencia> filtradas = new ArrayList<>();

        for (Incidencia i : origen) {

            // Búsqueda texto
            if (!busq.isBlank()) {
                String titulo = i.getTitulo() != null ? i.getTitulo().toLowerCase() : "";
                String solicitante = i.getNombreSolicitante() != null ? i.getNombreSolicitante().toLowerCase() : "";
                if (!titulo.contains(busq) && !solicitante.contains(busq)) {
                    continue;
                }
            }

            // Categoría
            if (cat != null && !cat.equals("Todas")) {
                if (i.getCategoria() == null || !i.getCategoria().name().equals(cat)) {
                    continue;
                }
            }

            // Prioridad
            if (prio != null && !prio.equals("Todas")) {
                if (i.getPrioridad() == null || !i.getPrioridad().name().equals(prio)) {
                    continue;
                }
            }

            // Estado
            if (est != null && !est.equals("Todas") && !est.equals("Todos")) {
                if (i.getEstado() == null || !i.getEstado().name().equals(est)) {
                    continue;
                }
            }

            // Técnico
            if (tec != null && !tec.equals("Todos")) {
                if (i.getTecnicoAsignadoId() == null) {
                    continue;
                }
                Tecnico t = service.buscarTecnicoPorId(i.getTecnicoAsignadoId()).orElse(null);
                if (t == null || !t.getNombreCompleto().equals(tec)) {
                    continue;
                }
            }

            // Rango fechas
            if (desde != null && i.getFechaApertura().isBefore(desde)) {
                continue;
            }
            if (hasta != null && !i.getFechaApertura().isBefore(hasta)) {
                continue;
            }

            filtradas.add(i);
        }

        datos.setAll(filtradas);
    }

    private void limpiarFiltros() {
        tfBusqueda.clear();
        cbFiltroCategoria.setValue("Todas");
        cbFiltroPrioridad.setValue("Todas");
        cbFiltroEstado.setValue("Todas");
        cbFiltroTecnico.setValue("Todos");
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        aplicarFiltros();
    }

    // ─────────────────────────────────────────────────────────────
    // OPCIONES DE FILTRO
    // ─────────────────────────────────────────────────────────────

    private void setupCombo(ComboBox<String> cb, String placeholder, List<String> opts) {
        cb.getItems().setAll(opts);
        cb.setValue(opts.get(0));
        cb.getStyleClass().add("form-field");
        cb.setPrefWidth(120);
        cb.valueProperty().addListener((o, v, n) -> aplicarFiltros());
    }

    private List<String> getCategoriasOpts() {
        List<String> l = new ArrayList<>();
        l.add("Todas");
        for (Categoria c : Categoria.values()) {
            l.add(c.name());
        }
        return l;
    }

    private List<String> getPrioridadesOpts() {
        List<String> l = new ArrayList<>();
        l.add("Todas");
        for (Prioridad p : Prioridad.values()) {
            l.add(p.name());
        }
        return l;
    }

    private List<String> getEstadosOpts() {
        List<String> l = new ArrayList<>();
        l.add("Todas");
        for (Estado e : Estado.values()) {
            l.add(e.name());
        }
        return l;
    }

    private List<String> getTecnicosOpts() {
        List<String> l = new ArrayList<>();
        l.add("Todos");
        for (Tecnico t : service.listarTecnicos()) {
            l.add(t.getNombreCompleto());
        }
        return l;
    }

    public void cargarDatos() {
        datos.setAll(service.listarIncidencias());
        aplicarFiltros();

        String prev = cbFiltroTecnico.getValue();
        cbFiltroTecnico.getItems().setAll(getTecnicosOpts());
        cbFiltroTecnico.setValue(prev != null ? prev : "Todos");
    }
}

