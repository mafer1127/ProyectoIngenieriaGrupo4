package com.helpdesk.ui.views;

import com.helpdesk.model.Tecnico;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.ui.components.TecnicoFormDialog;
import com.helpdesk.util.UIHelper;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class GestionTecnicosView extends BorderPane {

    private final IncidenciaService service;
    private final TableView<Tecnico> tabla = new TableView<>();
    private final ObservableList<Tecnico> datos = FXCollections.observableArrayList();

    public GestionTecnicosView(IncidenciaService service) {
        this.service = service;
        construirUI();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void construirUI() {
        // Top bar
        HBox topBar = new HBox(12);
        topBar.setPadding(new Insets(14, 20, 14, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getStyleClass().add("topbar");

        Label title = new Label("Gestión de Técnicos");
        title.getStyleClass().add("topbar-title");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnNuevo = new Button("＋  Nuevo técnico");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> abrirFormNuevo());

        topBar.getChildren().addAll(title, spacer, btnNuevo);
        setTop(topBar);

        // Tabla
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getStyleClass().add("table-view");
        tabla.setItems(datos);

        TableColumn<Tecnico, String> colNombre = new TableColumn<>("Nombre completo");
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colNombre.setPrefWidth(200);

        TableColumn<Tecnico, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmailCorporativo()));
        colEmail.setPrefWidth(220);

        TableColumn<Tecnico, String> colEsp = new TableColumn<>("Especialidad");
        colEsp.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEspecialidad() == null ? "—" :
                c.getValue().getEspecialidad().name().replace("_", " ")));
        colEsp.setPrefWidth(150);

        TableColumn<Tecnico, String> colActivo = new TableColumn<>("Estado");
        colActivo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isActivo() ? "Activo" : "Inactivo"));
        colActivo.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                Label l = new Label(item);
                l.getStyleClass().addAll("badge",
                        "Activo".equals(item) ? "badge-resuelta" : "badge-cerrada");
                HBox b = new HBox(l); b.setAlignment(Pos.CENTER_LEFT);
                setGraphic(b); setText(null);
            }
        });
        colActivo.setPrefWidth(90);

        TableColumn<Tecnico, Void> colAcciones = new TableColumn<>("");
            colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar = new Button("✏");
            private final Button btnElim = new Button("🗑");

            private final HBox box = new HBox(6, btnEditar, btnElim);

            {
                btnEditar.getStyleClass().add("btn-icon");
                btnElim.getStyleClass().add("btn-icon");

                // ─── TOOLTIP COMO YOUTUBE ───────────────────────────────
                Tooltip.install(btnEditar, new Tooltip("Editar técnico"));
                Tooltip.install(btnElim,   new Tooltip("Eliminar técnico"));

                box.setAlignment(Pos.CENTER);

                btnEditar.setOnAction(e -> editarTecnico(getTableView().getItems().get(getIndex())));
                btnElim.setOnAction(e -> eliminarTecnico(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
                setText(null);
            }
        });
        colAcciones.setMinWidth(120);
        colAcciones.setPrefWidth(120);
        colAcciones.setMaxWidth(120);

        tabla.getColumns().addAll(colNombre, colEmail, colEsp, colActivo, colAcciones);

        VBox center = new VBox(tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        center.setPadding(new Insets(16));
        center.setStyle("-fx-background-color: transparent;");
        setCenter(center);
    }

    private void abrirFormNuevo() {
        new TecnicoFormDialog(null).showAndWait().ifPresent(t -> {
            try { service.crearTecnico(t); cargarDatos(); }
            catch (Exception ex) { UIHelper.alertError("Error", ex.getMessage()); }
        });
    }

    private void editarTecnico(Tecnico t) {
        new TecnicoFormDialog(t).showAndWait().ifPresent(updated -> {
            try { service.actualizarTecnico(updated); cargarDatos(); }
            catch (Exception ex) { UIHelper.alertError("Error", ex.getMessage()); }
        });
    }

    private void toggleActivo(Tecnico t) {
        t.setActivo(!t.isActivo());
        try { service.actualizarTecnico(t); cargarDatos(); }
        catch (Exception ex) { UIHelper.alertError("Error", ex.getMessage()); }
    }

    private void eliminarTecnico(Tecnico t) {
        if (UIHelper.confirm("Eliminar técnico",
                "¿Eliminar a " + t.getNombreCompleto() + "?")) {
            try { service.eliminarTecnico(t.getId()); cargarDatos(); }
            catch (Exception ex) { UIHelper.alertError("Error", ex.getMessage()); }
        }
    }

    public void cargarDatos() {
        datos.setAll(service.listarTecnicos());
    }
}
