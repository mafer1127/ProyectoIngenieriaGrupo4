package com.helpdesk.ui.views;

import java.util.Map;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.service.IncidenciaService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class EstadisticasSLAView extends BorderPane {

    private final IncidenciaService service;
    private final VBox contenido = new VBox(24);

    public EstadisticasSLAView(IncidenciaService service) {
        this.service = service;
        construirUI();
        cargar();
    }

    private void construirUI() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("topbar");
        Label title = new Label("Estadísticas SLA");
        title.getStyleClass().add("topbar-title");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        javafx.scene.control.Button btnR = new javafx.scene.control.Button("↻  Actualizar");
        btnR.getStyleClass().add("btn-secondary");
        btnR.setOnAction(e -> cargar());
        topBar.getChildren().addAll(title, sp, btnR);
        setTop(topBar);

        contenido.setPadding(new Insets(24));
        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(contenido);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setCenter(scroll);
    }

    public void cargar() {
        contenido.getChildren().clear();

        // ── SLA críticas ──────────────────────────────────────────────────────
        double pctSLA = service.porcentajeSLACriticas();
        Label secSLA = new Label("SLA — INCIDENCIAS CRÍTICAS RESUELTAS EN < 4H");
        secSLA.getStyleClass().add("detail-section-title");
        contenido.getChildren().add(secSLA);

        HBox slaCard = new HBox(20);
        slaCard.setAlignment(Pos.CENTER_LEFT);

        VBox cardPct = tarjeta(String.format("%.1f%%", pctSLA), "Cumplimiento SLA críticas",
                pctSLA >= 80 ? "#22c55e" : pctSLA >= 50 ? "#f59e0b" : "#ef4444");
        slaCard.getChildren().add(cardPct);

        // Gráfico de pastel SLA
        PieChart pie = new PieChart();
        pie.setTitle("SLA Críticas");
        pie.setLegendVisible(true);
        pie.setPrefSize(280, 220);
        pie.setStyle("-fx-background-color: transparent;");
        long total = service.listarIncidencias().stream()
                .filter(i -> i.getPrioridad() == Incidencia.Prioridad.CRITICA && i.getFechaResolucion() != null).count();
        long cumple = Math.round(total * pctSLA / 100.0);
        if (total > 0) {
            pie.getData().add(new PieChart.Data("Cumple SLA (" + cumple + ")", cumple));
            pie.getData().add(new PieChart.Data("Incumple (" + (total-cumple) + ")", total - cumple));
        } else {
            pie.getData().add(new PieChart.Data("Sin datos", 1));
        }
        slaCard.getChildren().add(pie);
        contenido.getChildren().add(slaCard);

        // ── Media por técnico ─────────────────────────────────────────────────
        Label secTec = new Label("TIEMPO MEDIO DE RESOLUCIÓN POR TÉCNICO (horas)");
        secTec.getStyleClass().add("detail-section-title");
        contenido.getChildren().add(secTec);

        Map<String, Double> mediasTec = service.mediaHorasPorTecnico();
        if (mediasTec.isEmpty()) {
            Label vacio = new Label("Sin datos suficientes.");
            vacio.setStyle("-fx-text-fill: #7A8BB5; -fx-font-size: 12px;");
            contenido.getChildren().add(vacio);
        } else {
            CategoryAxis xAxis = new CategoryAxis();
            NumberAxis   yAxis = new NumberAxis();
            xAxis.setLabel("Técnico"); yAxis.setLabel("Horas");
            xAxis.setStyle("-fx-tick-label-fill: #7a92aa;");
            yAxis.setStyle("-fx-tick-label-fill: #7a92aa;");

            BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
            bar.setTitle("Media horas/técnico");
            bar.setStyle("-fx-background-color: transparent;");
            bar.setPrefHeight(280);
            bar.setLegendVisible(false);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            mediasTec.forEach((tecId, media) -> {
                String nombre = service.buscarTecnicoPorId(tecId)
                        .map(Tecnico::getNombreCompleto).orElse(tecId.length() > 8 ? tecId.substring(0, 8) : tecId);
                series.getData().add(new XYChart.Data<>(nombre, media));
            });
            bar.getData().add(series);
            bar.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 8;");
            contenido.getChildren().add(bar);
        }

    }

    private VBox tarjeta(String valor, String label, String color) {
        Label num = new Label(valor);
        num.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #7A8BB5;");
        lbl.setWrapText(true);
        VBox card = new VBox(4, num, lbl);
        card.getStyleClass().add("stat-card");
        card.setMinWidth(160);
        return card;
    }
}
