package com.helpdesk.util;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Incidencia.Estado;
import com.helpdesk.model.Incidencia.Prioridad;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class UIHelper {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private UIHelper() {}

    public static String fmt(LocalDateTime dt) {
        return dt == null ? "—" : dt.format(FMT);
    }

    public static Label badgePrioridad(Prioridad p) {
        if (p == null) return new Label("—");
        Label l = new Label(p.name());
        l.getStyleClass().addAll("badge", "badge-" + p.name().toLowerCase());
        return l;
    }

    public static Label badgeEstado(Estado e) {
        if (e == null) return new Label("—");
        Label l = new Label(e.name().replace("_", " "));
        l.getStyleClass().addAll("badge", "badge-" + e.name().toLowerCase());
        return l;
    }

    public static HBox badgePrioridadBox(Prioridad p) {
        HBox box = new HBox(badgePrioridad(p));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public static HBox badgeEstadoBox(Estado e) {
        HBox box = new HBox(badgeEstado(e));
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public static String primeraMayuscula(String s) {
        if (s == null || s.isBlank()) return "";
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void alertError(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        styleAlert(a);
        a.showAndWait();
    }

    public static void alertInfo(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        styleAlert(a);
        a.showAndWait();
    }

    public static boolean confirm(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        styleAlert(a);
        return a.showAndWait().filter(r -> r == javafx.scene.control.ButtonType.OK).isPresent();
    }

    private static void styleAlert(Alert a) {
        a.getDialogPane().setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-border-color: #D5DEFD;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
        a.getDialogPane().lookupAll(".label")
         .forEach(n -> ((Label) n).setStyle("-fx-text-fill: #1A2050;"));
        a.getDialogPane().lookupAll(".button").forEach(n -> {
            n.setStyle(
                "-fx-background-color: #2450E8;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
            );
        });
    }
}
