package com.helpdesk.ui.views;

import com.helpdesk.model.HistorialCambio;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.util.UIHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.util.List;

public class HistorialView extends VBox {

    private final IncidenciaService service;
    private final TableView<HistorialCambio> tabla = new TableView<>();

    public HistorialView(IncidenciaService service) {
        this.service = service;
        setStyle("-fx-background-color: transparent;");
        setPadding(new Insets(16));
        setSpacing(10);

        Label titulo = new Label("HISTORIAL DE CAMBIOS");
        titulo.getStyleClass().add("detail-section-title");

        construirTabla();
        VBox.setVgrow(tabla, Priority.ALWAYS);
        getChildren().addAll(titulo, tabla);
    }

    @SuppressWarnings("unchecked")
    private void construirTabla() {
        tabla.getStyleClass().add("table-view");
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<HistorialCambio, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                UIHelper.fmt(c.getValue().getFecha())));
        colFecha.setPrefWidth(140);

        TableColumn<HistorialCambio, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario()));
        colUsuario.setPrefWidth(150);

        TableColumn<HistorialCambio, String> colAnterior = new TableColumn<>("Estado anterior");
        colAnterior.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstadoAnterior() == null ? "—" :
                c.getValue().getEstadoAnterior().name().replace("_"," ")));
        colAnterior.setPrefWidth(130);

        TableColumn<HistorialCambio, String> colNuevo = new TableColumn<>("Estado nuevo");
        colNuevo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstadoNuevo() == null ? "—" :
                c.getValue().getEstadoNuevo().name().replace("_"," ")));
        colNuevo.setPrefWidth(130);

        TableColumn<HistorialCambio, String> colNota = new TableColumn<>("Nota / Solución");
        colNota.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getNota() == null ? "" : c.getValue().getNota()));

        tabla.getColumns().addAll(colFecha, colUsuario, colAnterior, colNuevo, colNota);
    }

    public void cargar(String incidenciaId) {
        List<HistorialCambio> lista = service.historialDeIncidencia(incidenciaId);
        tabla.setItems(FXCollections.observableArrayList(lista));
    }
}
