package com.helpdesk.controller;

import java.io.File;
import java.time.LocalDate;

import com.helpdesk.service.ExportService;
import com.helpdesk.ui.components.ExportDialog;
import com.helpdesk.util.UIHelper;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    public void abrirDialogo(Window owner) {
        ExportDialog dlg = new ExportDialog();
        String formato = dlg.showAndWait().orElse(null);
        if (formato == null) return;

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home")));

        switch (formato) {
            case "CSV" -> exportarCSV(fc, owner);
            case "XLSX" -> exportarXLSX(fc, owner);
            case "PDF" -> exportarPDF(fc, owner);
        }
    }

    private void exportarCSV(FileChooser fc, Window owner) {
        fc.setTitle("Guardar CSV");
        fc.setInitialFileName("incidencias_" + nombreMesActual() + "_" + LocalDate.now().getYear() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));

        File f = fc.showSaveDialog(owner);
        if (f == null) return;

        try {
            exportService.exportarMesActualCSV(f.getAbsolutePath());
            UIHelper.alertInfo("Exportación completa", "Archivo guardado:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            UIHelper.alertError("Error al exportar CSV", ex.getMessage());
        }
    }

    private void exportarXLSX(FileChooser fc, Window owner) {
        fc.setTitle("Guardar XLSX");
        fc.setInitialFileName("incidencias_" + nombreMesActual() + "_" + LocalDate.now().getYear() + ".xlsx");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));

        File f = fc.showSaveDialog(owner);
        if (f == null) return;

        try {
            exportService.exportarMesActualXLSX(f.getAbsolutePath());
            UIHelper.alertInfo("Exportación completa", "Archivo guardado:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            UIHelper.alertError("Error al exportar XLSX", ex.getMessage());
        }
    }

    private void exportarPDF(FileChooser fc, Window owner) {
        fc.setTitle("Guardar PDF");
        fc.setInitialFileName("informe_" + nombreMesActual() + "_" + LocalDate.now().getYear() + ".pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

        File f = fc.showSaveDialog(owner);
        if (f == null) return;

        try {
            exportService.exportarInformeMensualPDF(f.getAbsolutePath());
            UIHelper.alertInfo("PDF generado", "Archivo guardado:\n" + f.getAbsolutePath());
        } catch (Exception ex) {
            UIHelper.alertError("Error al generar PDF", ex.getMessage());
        }
    }

    private String nombreMesActual() {
        LocalDate hoy = LocalDate.now();
        return hoy.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es"));
    }
}