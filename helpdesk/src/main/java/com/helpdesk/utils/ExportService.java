package com.helpdesk.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.helpdesk.model.Incidencia;

public class ExportService {

    // Exportar CSV
    public void exportarCSV(List<Incidencia> incidencias) {
        String nombreArchivo = "incidencias_mes.csv";

        try (OutputStreamWriter osw = new OutputStreamWriter(
            new FileOutputStream(nombreArchivo), StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(osw)) {

            //Añadir BOM para que Excel reconozca UTF‑8
            osw.write('\uFEFF');
            

            //Añadir BOM para que Excel reconozca UTF‑8
            osw.write('\uFEFF');

            writer.println("ID;Título;Descripción;Categoría;Prioridad;Solicitante;Email;Estado;Fecha Apertura;Fecha Cierre;Técnico;Solución;Tiempo Resolución");

            for (Incidencia i : incidencias) {
                writer.printf("%d;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
                        i.getId(),
                        i.getTitulo(),
                        i.getDescripcion(),
                        i.getCategoria(),
                        i.getPrioridad(),
                        i.getSolicitante(),
                        i.getEmailSolicitante(),
                        i.getEstado(),
                        i.getFechaApertura(),
                        i.getFechaCierre() != null ? i.getFechaCierre() : "—",
                        i.getTecnicoAsignado() != null ? i.getTecnicoAsignado().getNombre() + " " + i.getTecnicoAsignado().getApellidos() : "Sin asignar",
                        i.getSolucion() != null ? i.getSolucion() : "—",
                        i.getFechaCierre() != null ? i.getTiempoResolucion() : "—"
                );
            }

            System.out.println("CSV exportado correctamente: " + nombreArchivo);

        } catch (IOException e) {
            System.out.println("Error al exportar CSV: " + e.getMessage());
        }
    }

    // Exportar XLSX con colores
    public void exportarXLSX(List<Incidencia> incidencias) {
        String nombreArchivo = "incidencias_mes.xlsx";

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Incidencias del mes");

        CellStyle wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        // Limitar ancho de columna de descripción
        sheet.setColumnWidth(2, 40 * 256);
        //Limitar ancho de columna de solución
        sheet.setColumnWidth(11, 40 * 256);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        String[] columnas = {
            "ID", "Título", "Descripción", "Categoría", "Prioridad",
            "Solicitante", "Email", "Estado", "Fecha Apertura",
            "Fecha Cierre", "Técnico", "Solución", "Tiempo Resolución"
        };

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;
        for (Incidencia i : incidencias) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(i.getId());
            row.createCell(1).setCellValue(i.getTitulo());
            Cell descCell = row.createCell(2);
            descCell.setCellValue(i.getDescripcion());
            descCell.setCellStyle(wrapStyle);
            row.createCell(3).setCellValue(i.getCategoria().toString());
            row.createCell(4).setCellValue(i.getPrioridad().toString());
            row.createCell(5).setCellValue(i.getSolicitante());
            row.createCell(6).setCellValue(i.getEmailSolicitante());
            row.createCell(7).setCellValue(i.getEstado().toString());
            row.createCell(8).setCellValue(i.getFechaApertura().toString());
            row.createCell(9).setCellValue(
                    i.getFechaCierre() != null ? i.getFechaCierre().toString() : "—"
            );
            row.createCell(10).setCellValue(
                    i.getTecnicoAsignado() != null ?
                            i.getTecnicoAsignado().getNombre() + " " + i.getTecnicoAsignado().getApellidos()
                            : "Sin asignar"
            );
            Cell solCell = row.createCell(11);
            solCell.setCellValue(i.getSolucion() != null ? i.getSolucion() : "—");
            solCell.setCellStyle(wrapStyle);
            row.createCell(12).setCellValue(
                    i.getFechaCierre() != null ? i.getTiempoResolucion() : "—"
            );
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fileOut = new FileOutputStream(nombreArchivo)) {
            workbook.write(fileOut);
            workbook.close();
            System.out.println("XLSX exportado correctamente: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al exportar XLSX: " + e.getMessage());
        }
    }
}
