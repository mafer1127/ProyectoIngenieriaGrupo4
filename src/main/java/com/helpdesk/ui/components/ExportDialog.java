package com.helpdesk.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

public class ExportDialog extends Dialog<String> {

    public ExportDialog() {
        setTitle("Exportar incidencias del mes");
        initModality(Modality.APPLICATION_MODAL);

        DialogPane pane = getDialogPane();
        pane.setPrefWidth(520);   
        pane.setPrefHeight(300);  
        pane.setStyle("-fx-background-color: white;");

        // Botón "oficial" de cancelar para que funcionen la X y ESC
        ButtonType btnTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        pane.getButtonTypes().add(btnTypeCancel);

        // Contenido personalizado
        VBox root = new VBox(24);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_LEFT);

        Label titulo = new Label("Exportar incidencias del mes");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1A2E4A;");

        Label subtitulo = new Label("Selecciona el formato de exportación");
        subtitulo.setStyle("-fx-font-size: 13px; -fx-text-fill: #6B7A99;");

        // Botones estilo Figma
        Button btnCSV = new Button("CSV");
        Button btnXLSX = new Button("XLSX");
        Button btnPDF = new Button("PDF");

        btnCSV.setMinWidth(110);
        btnXLSX.setMinWidth(110);
        btnPDF.setMinWidth(110);

        btnCSV.getStyleClass().add("btn-primary");
        btnXLSX.getStyleClass().add("btn-primary");
        btnPDF.getStyleClass().add("btn-primary");

        btnCSV.setOnAction(e -> { setResult("CSV"); close(); });
        btnXLSX.setOnAction(e -> { setResult("XLSX"); close(); });
        btnPDF.setOnAction(e -> { setResult("PDF"); close(); });

        HBox botones = new HBox(10, btnCSV, btnXLSX, btnPDF);
        botones.setAlignment(Pos.CENTER);

        root.getChildren().addAll(titulo, subtitulo, botones);

        getDialogPane().setContent(root);
    
    }
}