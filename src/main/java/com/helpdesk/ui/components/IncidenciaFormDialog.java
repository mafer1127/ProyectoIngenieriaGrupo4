package com.helpdesk.ui.components;

import java.util.List;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Incidencia.Categoria;
import com.helpdesk.model.Incidencia.Prioridad;
import com.helpdesk.model.Tecnico;
import com.helpdesk.service.IncidenciaService;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

public class IncidenciaFormDialog extends Dialog<Incidencia> {

    private final TextField tfTitulo = new TextField();
    private final TextArea taDesc = new TextArea();
    private final ComboBox<Categoria> cbCat = new ComboBox<>();
    private final ComboBox<Prioridad> cbPrio = new ComboBox<>();
    private final TextField tfNombre = new TextField();
    private final TextField tfEmail = new TextField();
    private final ComboBox<Tecnico> cbTec = new ComboBox<>();
    private final Label lblError = new Label();

    public IncidenciaFormDialog(IncidenciaService service, Incidencia existente) {

        setTitle(existente == null ? "Nueva Incidencia" : "Editar Incidencia");
        initModality(Modality.APPLICATION_MODAL);

        DialogPane dp = getDialogPane();
        dp.getStylesheets().add(getClass().getResource("/com/helpdesk/css/styles.css").toExternalForm());
        dp.getStyleClass().add("form-pane");
        dp.setPrefWidth(540);

        // ─────────────────────────────────────────────────────────────
        // CAMPOS
        // ─────────────────────────────────────────────────────────────

        cbCat.getItems().addAll(Categoria.values());
        cbPrio.getItems().addAll(Prioridad.values());

        cbTec.getItems().add(null);
        List<Tecnico> activos = service.listarTecnicosActivos();
        cbTec.getItems().addAll(activos);

        cbTec.setCellFactory(lv -> tecCell());
        cbTec.setButtonCell(tecCell());

        styleField(tfTitulo);
        styleField(taDesc);
        taDesc.setWrapText(true);
        taDesc.setPrefRowCount(3);

        cbCat.getStyleClass().add("form-field");
        cbPrio.getStyleClass().add("form-field");
        cbTec.getStyleClass().add("form-field");

        styleField(tfNombre);
        styleField(tfEmail);

        lblError.getStyleClass().add("form-error");
        lblError.setWrapText(true);

        // ─────────────────────────────────────────────────────────────
        // CARGAR DATOS SI ES EDICIÓN
        // ─────────────────────────────────────────────────────────────

        if (existente != null) {
            tfTitulo.setText(existente.getTitulo());
            taDesc.setText(existente.getDescripcion());
            cbCat.setValue(existente.getCategoria());
            cbPrio.setValue(existente.getPrioridad());
            tfNombre.setText(existente.getNombreSolicitante());
            tfEmail.setText(existente.getEmailSolicitante());

            if (existente.getTecnicoAsignadoId() != null) {
                for (Tecnico t : activos) {
                    if (t.getId().equals(existente.getTecnicoAsignadoId())) {
                        cbTec.setValue(t);
                        break;
                    }
                }
            }
        }

        // ─────────────────────────────────────────────────────────────
        // OCULTAR CAMPO TÉCNICO SI ES USUARIO FINAL
        // ─────────────────────────────────────────────────────────────

        var rol = com.helpdesk.service.SessionManager.getInstance().getUsuario().getRol();

        if (rol == com.helpdesk.model.Usuario.Rol.USUARIO_FINAL) {
            cbTec.setVisible(false);
            cbTec.setManaged(false);
        }

        // ─────────────────────────────────────────────────────────────
        // GRID
        // ─────────────────────────────────────────────────────────────

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);

        ColumnConstraints c1 = new ColumnConstraints(130);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);

        int row = 0;
        addRow(grid, row++, "Título *", tfTitulo);
        addRow(grid, row++, "Descripción *", taDesc);
        addRow(grid, row++, "Categoría *", cbCat);
        addRow(grid, row++, "Prioridad *", cbPrio);
        addRow(grid, row++, "Solicitante *", tfNombre);
        addRow(grid, row++, "Email *", tfEmail);
        addRow(grid, row++, "Técnico", cbTec);

        if (rol == com.helpdesk.model.Usuario.Rol.USUARIO_FINAL) {
            // El label está en la columna 0 de esa fila
            ((Label) grid.getChildren().get((row - 1) * 2)).setVisible(false);
            ((Label) grid.getChildren().get((row - 1) * 2)).setManaged(false);
        }

        grid.add(lblError, 0, row, 2, 1);

        ScrollPane sp = new ScrollPane(grid);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        dp.setContent(sp);

        // ─────────────────────────────────────────────────────────────
        // BOTONES
        // ─────────────────────────────────────────────────────────────

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dp.getButtonTypes().addAll(btnGuardar, btnCancelar);

        Button bG = (Button) dp.lookupButton(btnGuardar);
        bG.getStyleClass().add("btn-primary");
        ((Button) dp.lookupButton(btnCancelar)).getStyleClass().add("btn-secondary");

        bG.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            String err = validar();
            if (err != null) {
                lblError.setText(err);
                ev.consume();
            }
        });

        // ─────────────────────────────────────────────────────────────
        // RESULTADO
        // ─────────────────────────────────────────────────────────────

        setResultConverter(bt -> {
            if (bt != btnGuardar) return null;

            Incidencia inc = existente != null
                    ? existente
                    : new Incidencia(
                        tfTitulo.getText().trim(),
                        taDesc.getText().trim(),
                        cbCat.getValue(),
                        cbPrio.getValue(),
                        tfNombre.getText().trim(),
                        tfEmail.getText().trim()
                    );

            if (existente != null) {
                inc.setTitulo(tfTitulo.getText().trim());
                inc.setDescripcion(taDesc.getText().trim());
                inc.setCategoria(cbCat.getValue());
                inc.setPrioridad(cbPrio.getValue());
                inc.setNombreSolicitante(tfNombre.getText().trim());
                inc.setEmailSolicitante(tfEmail.getText().trim());
            }

            Tecnico tec = cbTec.getValue();
            if (existente != null) {
                if (tec != null) {
                    service.asignarTecnico(inc.getId(), tec.getId());
                } else {
                    inc.setTecnicoAsignadoId(null);
                }
            } else {
                // Nueva incidencia: solo guardar el técnico si existe
                inc.setTecnicoAsignadoId(tec != null ? tec.getId() : null);
            }

            return inc;
        });
    }

    // ─────────────────────────────────────────────────────────────
    // VALIDACIÓN
    // ─────────────────────────────────────────────────────────────

    private String validar() {
        if (tfTitulo.getText().isBlank()) return "El título es obligatorio.";
        if (taDesc.getText().isBlank()) return "La descripción es obligatoria.";
        if (cbCat.getValue() == null) return "Selecciona una categoría.";
        if (cbPrio.getValue() == null) return "Selecciona una prioridad.";
        if (tfNombre.getText().isBlank()) return "El nombre del solicitante es obligatorio.";
        if (!tfEmail.getText().trim().matches("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+$"))
            return "El email no tiene formato válido.";
        return null;
    }

    // ─────────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────────

    private void styleField(Control c) {
        c.getStyleClass().add("form-field");
        c.setMaxWidth(Double.MAX_VALUE);
    }

    private void addRow(GridPane g, int row, String label, javafx.scene.Node field) {
        Label l = new Label(label);
        l.getStyleClass().add("form-label");
        l.setAlignment(Pos.TOP_LEFT);
        g.add(l, 0, row);
        g.add(field, 1, row);
    }

    private ListCell<Tecnico> tecCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Tecnico t, boolean empty) {
                super.updateItem(t, empty);
                setText(empty || t == null ? "(Sin asignar)" : t.getNombreCompleto());
            }
        };
    }
}
