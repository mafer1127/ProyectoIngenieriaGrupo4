package com.helpdesk.ui.views;

import java.util.Set;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;
import com.helpdesk.ui.components.CambioEstadoDialog;
import com.helpdesk.ui.components.IncidenciaFormDialog;
import com.helpdesk.util.UIHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DetalleIncidenciaView extends BorderPane {

    private final IncidenciaService service;
    private final Runnable onVolver;
    private Incidencia incidencia;

    private final VBox contenido = new VBox(18);
    private final HistorialView historialView;

    public DetalleIncidenciaView(IncidenciaService service, Runnable onVolver) {
        this.service = service;
        this.onVolver = onVolver;
        this.historialView = new HistorialView(service);
        construirUI();
    }

    private void construirUI() {
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("topbar");

        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> { if (onVolver != null) onVolver.run(); });

        Label title = new Label("Detalle de incidencia");
        title.getStyleClass().add("topbar-title");

        topBar.getChildren().addAll(btnVolver, title);
        setTop(topBar);

        contenido.setPadding(new Insets(24));

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: transparent;");

        ScrollPane spDetalle = new ScrollPane(contenido);
        spDetalle.setFitToWidth(true);
        spDetalle.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Tab tabDetalle = new Tab("📋  Detalle", spDetalle);
        Tab tabHistorial = new Tab("📜  Historial", historialView);

        tabs.getTabs().addAll(tabDetalle, tabHistorial);
        setCenter(tabs);
    }

    public void cargar(Incidencia inc) {
        this.incidencia = service.buscarIncidenciaPorId(inc.getId()).orElse(inc);
        renderizar();
        historialView.cargar(incidencia.getId());
    }

    private void renderizar() {
        contenido.getChildren().clear();

        if (incidencia.esAlertaCritica()) {
            HBox banner = new HBox(10);
            banner.getStyleClass().add("alerta-banner");

            Label ico = new Label("⚠");
            ico.setStyle("-fx-text-fill: #ff2244; -fx-font-size: 16px;");

            Label msg = new Label("INCIDENCIA CRÍTICA: lleva más de 4 horas sin resolverse");
            msg.getStyleClass().add("alerta-banner-text");

            banner.getChildren().addAll(ico, msg);
            contenido.getChildren().add(banner);
        }

        Label lblTitulo = new Label(incidencia.getTitulo());
        lblTitulo.getStyleClass().add("detail-title");
        lblTitulo.setWrapText(true);

        HBox badges = new HBox(8,
                UIHelper.badgePrioridad(incidencia.getPrioridad()),
                UIHelper.badgeEstado(incidencia.getEstado())
        );
        badges.setAlignment(Pos.CENTER_LEFT);

        contenido.getChildren().addAll(lblTitulo, badges);

        // ─────────────────────────────────────────────────────────────
        // BOTONES (solo técnico o admin)
        // ─────────────────────────────────────────────────────────────

        HBox acciones = new HBox(10);
        acciones.setAlignment(Pos.CENTER_LEFT);

        if (SessionManager.getInstance().esTecnicoOAdmin()) {

            Button btnEditar = new Button("✏  Editar");
            btnEditar.getStyleClass().add("btn-secondary");
            btnEditar.setOnAction(e -> editarIncidencia());

            Button btnEstado = new Button("⇄  Cambiar estado");
            btnEstado.getStyleClass().add("btn-primary");
            btnEstado.setOnAction(e -> cambiarEstado());

            Set<Incidencia.Estado> permitidos = estadosPermitidos(incidencia.getEstado());
            btnEstado.setDisable(permitidos.isEmpty());

            acciones.getChildren().addAll(btnEditar, btnEstado);
        }

        contenido.getChildren().add(acciones);
        contenido.getChildren().add(new Separator());

        // ─────────────────────────────────────────────────────────────
        // GRID DE DATOS
        // ─────────────────────────────────────────────────────────────

        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(14);

        ColumnConstraints cc1 = new ColumnConstraints(160);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHgrow(Priority.ALWAYS);

        ColumnConstraints cc3 = new ColumnConstraints(160);
        ColumnConstraints cc4 = new ColumnConstraints();
        cc4.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(cc1, cc2, cc3, cc4);

        String tecNombre = incidencia.getTecnicoAsignadoId() == null
                ? "Sin asignar"
                : service.buscarTecnicoPorId(incidencia.getTecnicoAsignadoId())
                        .map(Tecnico::getNombreCompleto).orElse("—");

        int r = 0;

        addDato(grid, r, 0, "Categoría", incidencia.getCategoria() == null ? "—" : incidencia.getCategoria().name());
        addDato(grid, r, 2, "Prioridad", incidencia.getPrioridad() == null ? "—" : incidencia.getPrioridad().name());
        r++;

        addDato(grid, r, 0, "Estado", incidencia.getEstado().name());
        addDato(grid, r, 2, "Técnico asignado", tecNombre);
        r++;

        addDato(grid, r, 0, "Solicitante", incidencia.getNombreSolicitante());
        addDato(grid, r, 2, "Email", incidencia.getEmailSolicitante());
        r++;

        addDato(grid, r, 0, "Fecha apertura", UIHelper.fmt(incidencia.getFechaApertura()));
        addDato(grid, r, 2, "Fecha resolución", UIHelper.fmt(incidencia.getFechaResolucion()));
        r++;

        addDato(grid, r, 0, "Tiempo resolución", incidencia.getTiempoResolucionFormateado());
        addDato(grid, r, 2, "SLA",
                incidencia.getPrioridad() == Incidencia.Prioridad.CRITICA &&
                        incidencia.getFechaResolucion() != null
                        ? (incidencia.cumpleSLA() ? "Cumple" : "Incumple")
                        : "—"
        );

        contenido.getChildren().add(grid);
        contenido.getChildren().add(new Separator());

        // ─────────────────────────────────────────────────────────────
        // DESCRIPCIÓN
        // ─────────────────────────────────────────────────────────────

        addSeccion("Descripción del problema");

        Label descVal = new Label(incidencia.getDescripcion());
        descVal.getStyleClass().add("detail-value");
        descVal.setWrapText(true);

        contenido.getChildren().add(descVal);

        // ─────────────────────────────────────────────────────────────
        // SOLUCIÓN (si existe)
        // ─────────────────────────────────────────────────────────────

        if (incidencia.getDescripcionSolucion() != null && !incidencia.getDescripcionSolucion().isBlank()) {
            contenido.getChildren().add(new Separator());
            addSeccion("Solución aplicada");

            Label solVal = new Label(incidencia.getDescripcionSolucion());
            solVal.getStyleClass().add("detail-value");
            solVal.setWrapText(true);
            solVal.setStyle("-fx-text-fill: #22c55e;");

            contenido.getChildren().add(solVal);
        }
    }

    private void addSeccion(String t) {
        Label l = new Label(t.toUpperCase());
        l.getStyleClass().add("detail-section-title");
        contenido.getChildren().add(l);
    }

    private void addDato(GridPane g, int row, int col, String label, String value) {
        Label lbl = new Label(label.toUpperCase());
        lbl.getStyleClass().add("detail-section-title");

        Label val = new Label(value == null ? "—" : value);
        val.getStyleClass().add("detail-value");
        val.setWrapText(true);

        g.add(new VBox(3, lbl, val), col, row);
    }

    private void editarIncidencia() {
        new IncidenciaFormDialog(service, incidencia).showAndWait().ifPresent(updated -> {
            try {
                service.actualizarIncidencia(updated);
                cargar(updated);
            } catch (Exception ex) {
                UIHelper.alertError("Error", ex.getMessage());
            }
        });
    }

    private void cambiarEstado() {
        new CambioEstadoDialog(incidencia.getEstado(), estadosPermitidos(incidencia.getEstado()))
                .showAndWait().ifPresent(r -> {
                    try {
                        service.cambiarEstado(incidencia.getId(), r.nuevoEstado(), r.solucion());
                        cargar(incidencia);
                    } catch (Exception ex) {
                        UIHelper.alertError("Error", ex.getMessage());
                    }
                });
    }

    private Set<Incidencia.Estado> estadosPermitidos(Incidencia.Estado e) {
        return switch (e) {
            case ABIERTA -> Set.of(Incidencia.Estado.EN_CURSO, Incidencia.Estado.EN_ESPERA);
            case EN_CURSO -> Set.of(Incidencia.Estado.EN_ESPERA, Incidencia.Estado.RESUELTA);
            case EN_ESPERA -> Set.of(Incidencia.Estado.EN_CURSO, Incidencia.Estado.RESUELTA);
            case RESUELTA -> Set.of(Incidencia.Estado.CERRADA);
            case CERRADA -> Set.of();
        };
    }
}
