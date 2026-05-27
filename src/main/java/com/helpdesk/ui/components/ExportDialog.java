package com.helpdesk.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ExportDialog extends Dialog<String> {

    public ExportDialog() {
        setTitle("Exportar incidencias del mes");
        setHeaderText("Selecciona el formato de exportación");

        ButtonType btnCSV  = new ButtonType("CSV",  ButtonBar.ButtonData.OK_DONE);
        ButtonType btnXLSX = new ButtonType("XLSX", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnPDF  = new ButtonType("PDF",  ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().addAll(btnCSV, btnXLSX, btnPDF, btnCancel);

        VBox box = new VBox(12);
        box.setPadding(new Insets(10));
        box.getChildren().add(new Label("Elige el formato para exportar el informe mensual."));
        getDialogPane().setContent(box);

        setResultConverter(bt -> {
            if (bt == btnCSV)  return "CSV";
            if (bt == btnXLSX) return "XLSX";
            if (bt == btnPDF)  return "PDF";
            return null;
        });
    }
}