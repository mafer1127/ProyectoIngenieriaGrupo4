package com.helpdesk.ui.components;

import com.helpdesk.model.Tecnico;
import com.helpdesk.model.Tecnico.Especialidad;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

public class TecnicoFormDialog extends Dialog<Tecnico> {

    private final TextField tfNombre   = new TextField();
    private final TextField tfApell    = new TextField();
    private final TextField tfEmail    = new TextField();
    private final ComboBox<Especialidad> cbEsp = new ComboBox<>();
    private final CheckBox chkActivo   = new CheckBox("Activo");
    private final Label    lblError    = new Label();

    public TecnicoFormDialog(Tecnico existente) {
        setTitle(existente == null ? "Nuevo Técnico" : "Editar Técnico");
        initModality(Modality.APPLICATION_MODAL);

        DialogPane dp = getDialogPane();
        dp.getStylesheets().add(getClass().getResource("/com/helpdesk/css/styles.css").toExternalForm());
        dp.getStyleClass().add("form-pane");
        dp.setPrefWidth(460);

        cbEsp.getItems().addAll(Especialidad.values());
        cbEsp.getStyleClass().add("form-field"); cbEsp.setMaxWidth(Double.MAX_VALUE);
        styleField(tfNombre); styleField(tfApell); styleField(tfEmail);
        chkActivo.setStyle("-fx-text-fill: #1A2050;");
        lblError.getStyleClass().add("form-error");

        chkActivo.setSelected(existente == null || existente.isActivo());

        if (existente != null) {
            tfNombre.setText(existente.getNombre());
            tfApell.setText(existente.getApellidos());
            tfEmail.setText(existente.getEmailCorporativo());
            cbEsp.setValue(existente.getEspecialidad());
        }

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        ColumnConstraints c1 = new ColumnConstraints(130);
        ColumnConstraints c2 = new ColumnConstraints(); c2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(c1, c2);

        int r = 0;
        addRow(grid, r++, "Nombre *",       tfNombre);
        addRow(grid, r++, "Apellidos *",    tfApell);
        addRow(grid, r++, "Email *",        tfEmail);
        addRow(grid, r++, "Especialidad *", cbEsp);
        grid.add(chkActivo, 1, r++);
        grid.add(lblError,  0, r, 2, 1);

        dp.setContent(grid);

        ButtonType btnOk  = new ButtonType("Guardar",  ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCan = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnOk, btnCan);

        Button bOk = (Button) dp.lookupButton(btnOk);
        bOk.getStyleClass().add("btn-primary");
        ((Button) dp.lookupButton(btnCan)).getStyleClass().add("btn-secondary");

        bOk.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            String err = validar();
            if (err != null) { lblError.setText(err); ev.consume(); }
        });

        setResultConverter(bt -> {
            if (bt == btnOk) {
                Tecnico t = existente != null ? existente : new Tecnico();
                if (existente == null) {
                    return new Tecnico(tfNombre.getText().trim(), tfApell.getText().trim(),
                                       tfEmail.getText().trim(), cbEsp.getValue());
                } else {
                    t.setNombre(tfNombre.getText().trim());
                    t.setApellidos(tfApell.getText().trim());
                    t.setEmailCorporativo(tfEmail.getText().trim());
                    t.setEspecialidad(cbEsp.getValue());
                    t.setActivo(chkActivo.isSelected());
                    return t;
                }
            }
            return null;
        });
    }

    private String validar() {
        if (tfNombre.getText().isBlank())  return "El nombre es obligatorio.";
        if (tfApell.getText().isBlank())   return "Los apellidos son obligatorios.";
        String email = tfEmail.getText().trim();
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+$"))
            return "Email corporativo no válido.";
        if (cbEsp.getValue() == null) return "Selecciona una especialidad.";
        return null;
    }

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
}
