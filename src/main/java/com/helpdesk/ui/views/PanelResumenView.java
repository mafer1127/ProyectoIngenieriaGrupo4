package com.helpdesk.ui.views;

import java.util.List;
import java.util.Map;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Incidencia.Categoria;
import com.helpdesk.model.Incidencia.Prioridad;
import com.helpdesk.model.Usuario;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;
import com.helpdesk.util.UIHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PanelResumenView extends BorderPane {

    private final IncidenciaService service;
    private final VBox contenido = new VBox(24);

    public PanelResumenView(IncidenciaService service) {
        this.service = service;
        construirUI();
        cargar();
    }

    private void construirUI() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("topbar");

        Label title = new Label("Panel de Resumen");
        title.getStyleClass().add("topbar-title");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnRefresh = new Button("↻  Actualizar");
        btnRefresh.getStyleClass().add("btn-secondary");
        btnRefresh.setOnAction(e -> cargar());

        topBar.getChildren().addAll(title, spacer, btnRefresh);
        setTop(topBar);

        contenido.setPadding(new Insets(24));
        ScrollPane sp = new ScrollPane(contenido);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setCenter(sp);
    }

    public void cargar() {
        contenido.getChildren().clear();

        Usuario u = SessionManager.getInstance().getUsuario();
        boolean esUsuarioFinal = (u.getRol() == Usuario.Rol.USUARIO_FINAL);

        // ── Tarjetas por prioridad ────────────────────────────────────────────
        Label secPrioridad = new Label("INCIDENCIAS ABIERTAS POR PRIORIDAD");
        secPrioridad.getStyleClass().add("detail-section-title");
        contenido.getChildren().add(secPrioridad);

        Map<Prioridad, Integer> resumen = service.resumenAbiertasPorPrioridad();        
        HBox tarjetas = new HBox(16);
        tarjetas.setAlignment(Pos.CENTER_LEFT);

        for (Prioridad p : Prioridad.values()) {
            int count = resumen.containsKey(p) ? resumen.get(p) : 0;
            VBox card = crearTarjeta(String.valueOf(count), p.name(),
                    "stat-card-" + p.name().toLowerCase());
            tarjetas.getChildren().add(card);
        }

        // Total
        int total = 0;
        for (Integer v : resumen.values()) {total += v;}
        VBox cardTotal = crearTarjeta(String.valueOf(total), "TOTAL ABIERTAS", "");
        tarjetas.getChildren().add(cardTotal);

        contenido.getChildren().add(tarjetas);

        // ── SECCIONES SOLO PARA TÉCNICO ───────────────────────────────────────
        if (!esUsuarioFinal) {

            // ── Alertas críticas ──────────────────────────────────────────────────
            List<Incidencia> alertas = service.alertasCriticas();
            if (!alertas.isEmpty()) {
                Label secAlertas = new Label("⚠  ALERTAS CRÍTICAS (> 4h sin resolver)");
                secAlertas.setStyle("-fx-text-fill: #B91C1C; -fx-font-size: 12px; -fx-font-weight: bold;");
                contenido.getChildren().add(secAlertas);

                for (Incidencia a : alertas) {
                    HBox banner = new HBox(12);
                    banner.getStyleClass().add("alerta-banner");
                    Label ico  = new Label("⚠"); ico.setStyle("-fx-text-fill: #ff2244; -fx-font-size: 14px;");
                    Label msg  = new Label(a.getTitulo() + "  —  " +
                            UIHelper.fmt(a.getFechaApertura()) + "  —  " +
                            a.getNombreSolicitante());
                    msg.getStyleClass().add("alerta-banner-text");
                    banner.getChildren().addAll(ico, msg);
                    contenido.getChildren().add(banner);
                }
            }

            // ── Media de resolución por categoría ────────────────────────────────
            Label secMedia = new Label("MEDIA DE TIEMPO DE RESOLUCIÓN POR CATEGORÍA (horas)");
            secMedia.getStyleClass().add("detail-section-title");
            contenido.getChildren().add(secMedia);

            Map<Categoria, Double> medias = service.mediaHorasResolucionPorCategoria();
            if (medias.isEmpty()) {
                Label vacio = new Label("No hay incidencias resueltas todavía.");
                vacio.setStyle("-fx-text-fill: #7A8BB5; -fx-font-size: 12px;");
                contenido.getChildren().add(vacio);
            } else {
                GridPane gridMedias = new GridPane();
                gridMedias.setHgap(20); gridMedias.setVgap(10);
                ColumnConstraints cc1 = new ColumnConstraints(160), cc2 = new ColumnConstraints(120);
                gridMedias.getColumnConstraints().addAll(cc1, cc2);

                int row = 0;
                for (Map.Entry<Categoria, Double> e : medias.entrySet()) {
                    Label cat = new Label(e.getKey().name());
                    cat.setStyle("-fx-text-fill: #1A2050; -fx-font-size: 12px;");
                    Label val = new Label(String.format("%.1fh", e.getValue()));
                    val.setStyle("-fx-text-fill: #2450E8; -fx-font-weight: bold; -fx-font-size: 14px;");
                    gridMedias.add(cat, 0, row);
                    gridMedias.add(val, 1, row);
                    row++;
                }
                contenido.getChildren().add(gridMedias);
            }
        }    

        // ── Últimas 5 incidencias del mes ─────────────────────────────────────
        Label secMes = new Label("INCIDENCIAS DEL MES ACTUAL (últimas 5)");
        secMes.getStyleClass().add("detail-section-title");
        contenido.getChildren().add(secMes);

        List<Incidencia> delMes = service.incidenciasDelMesActual();
        if (delMes.isEmpty()) {
            Label vacio = new Label("No hay incidencias este mes.");
            vacio.setStyle("-fx-text-fill: #7A8BB5; -fx-font-size: 12px;");
            contenido.getChildren().add(vacio);
        } else {
            int shown = Math.min(5, delMes.size());
            for (int i = delMes.size() - 1; i >= delMes.size() - shown; i--) {
                Incidencia inc = delMes.get(i);
                HBox row2 = new HBox(12);
                row2.setAlignment(Pos.CENTER_LEFT);
                row2.setPadding(new Insets(8 ,12, 8, 12));
                row2.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 8; -fx-border-color: #D5DEFD; -fx-border-radius: 8; -fx-border-width: 1;");

                Label lblTit = new Label(inc.getTitulo());
                lblTit.setStyle("-fx-text-fill: #1A2050; -fx-font-size: 12px;");
                HBox.setHgrow(lblTit, Priority.ALWAYS);

                HBox badgesRow = new HBox(6,
                    UIHelper.badgePrioridad(inc.getPrioridad()),
                    UIHelper.badgeEstado(inc.getEstado()));
                badgesRow.setAlignment(Pos.CENTER_RIGHT);

                row2.getChildren().addAll(lblTit, badgesRow);
                contenido.getChildren().add(row2);
            }
        }
    }

    private VBox crearTarjeta(String numero, String etiqueta, String extraClass) {
        Label num = new Label(numero);
        num.getStyleClass().add("stat-card-number");

        Label lbl = new Label(etiqueta);
        lbl.getStyleClass().add("stat-card-label");

        VBox card = new VBox(4, num, lbl);
        card.getStyleClass().add("stat-card");
        if (!extraClass.isBlank()) card.getStyleClass().add(extraClass);
        card.setMinWidth(130);
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }
}
