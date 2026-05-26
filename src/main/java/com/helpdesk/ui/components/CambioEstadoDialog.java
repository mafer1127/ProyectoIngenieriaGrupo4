package com.helpdesk.ui.components;

import com.helpdesk.model.Incidencia.Estado;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.util.Set;

public class CambioEstadoDialog extends Dialog<CambioEstadoDialog.Resultado> {

    public record Resultado(Estado nuevoEstado, String solucion) {}

    private final ComboBox<Estado> cbEstado = new ComboBox<>();
    private final TextArea         taSol    = new TextArea();
    private final Label            lblError = new Label();

    public CambioEstadoDialog(Estado estadoActual, Set<Estado> permitidos) {
        setTitle("Cambiar estado de la incidencia");
        initModality(Modality.APPLICATION_MODAL);

        DialogPane dp = getDialogPane();
        dp.getStylesheets().add(getClass().getResource("/com/helpdesk/css/styles.css").toExternalForm());
        dp.getStyleClass().add("form-pane");
        dp.setPrefWidth(420);

        cbEstado.getItems().addAll(permitidos);
        cbEstado.getStyleClass().add("form-field");
        cbEstado.setMaxWidth(Double.MAX_VALUE);

        taSol.setPromptText("Describe la solución aplicada (obligatorio al resolver o cerrar)…");
        taSol.setPrefRowCount(4);
        taSol.getStyleClass().add("form-field");
        taSol.setWrapText(true);

        lblError.getStyleClass().add("form-error");

        Label lCurr = new Label("Estado actual: " + estadoActual.name().replace("_", " "));
        lCurr.setStyle("-fx-text-fill: #7A8BB5; -fx-font-size: 12px;");

        VBox box = new VBox(10,
            lCurr,
            new Label("Nuevo estado") {{ getStyleClass().add("form-label"); }},
            cbEstado,
            new Label("Descripción de solución") {{ getStyleClass().add("form-label"); }},
            taSol,
            lblError
        );
        box.setPadding(new Insets(4));
        dp.setContent(box);

        ButtonType btnOk  = new ButtonType("Aplicar",   ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCan = new ButtonType("Cancelar",  ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(btnOk, btnCan);

        Button bOk = (Button) dp.lookupButton(btnOk);
        bOk.getStyleClass().add("btn-primary");
        ((Button) dp.lookupButton(btnCan)).getStyleClass().add("btn-secondary");

        bOk.addEventFilter(javafx.event.ActionEvent.ACTION, ev -> {
            if (cbEstado.getValue() == null) { lblError.setText("Selecciona un estado."); ev.consume(); return; }
            Estado ns = cbEstado.getValue();
            if ((ns == Estado.RESUELTA || ns == Estado.CERRADA) && taSol.getText().isBlank()) {
                lblError.setText("La descripción de solución es obligatoria al resolver o cerrar.");
                ev.consume();
            }
        });

        setResultConverter(bt -> bt == btnOk && cbEstado.getValue() != null
            ? new Resultado(cbEstado.getValue(), taSol.getText().trim())
            : null);
    }
}
