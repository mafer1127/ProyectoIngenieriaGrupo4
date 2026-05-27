package com.helpdesk.service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


public class ExportService {

    private final IncidenciaService incidenciaService;

    public ExportService(IncidenciaService service) {
        this.incidenciaService = service;
    }

    // ============================================================
    // EXPORTACIÓN A CSV (UTF‑8 con BOM)
    // ============================================================
    public void exportarMesActualCSV(String ruta) throws Exception {
        List<Incidencia> lista = incidenciaService.incidenciasDelMesActual();
        exportarListaCSV(ruta, lista);
    }

    private String safe(String s) {
        return s == null ? "" : s.replace(";", ",");
    }

    public void exportarListaCSV(String ruta, List<Incidencia> incidencias) throws Exception {

    try (OutputStreamWriter osw = new OutputStreamWriter(
            new FileOutputStream(ruta), StandardCharsets.UTF_8);
         PrintWriter writer = new PrintWriter(osw)) {

        // BOM para Excel
        osw.write('\uFEFF');

        writer.println("ID;Título;Descripción;Categoría;Prioridad;Solicitante;Email;Estado;Fecha Apertura;Fecha Cierre;Técnico;Solución;Tiempo Resolución");

        for (Incidencia i : incidencias) {

            String tecnico = "Sin asignar";
            if (i.getTecnicoAsignadoId() != null) {
                Tecnico t = incidenciaService.buscarTecnicoPorId(i.getTecnicoAsignadoId()).orElse(null);
                if (t != null) tecnico = t.getNombreCompleto();
            }

            writer.printf("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s%n",
                    i.getId(),
                    safe(i.getTitulo()),
                    safe(i.getDescripcion()),
                    i.getCategoria(),
                    i.getPrioridad(),
                    safe(i.getNombreSolicitante()),
                    safe(i.getEmailSolicitante()),
                    i.getEstado(),
                    i.getFechaApertura(),
                    i.getFechaResolucion() != null ? i.getFechaResolucion() : "—",
                    tecnico,
                    safe(i.getDescripcionSolucion()),
                    i.getFechaResolucion() != null ? i.getTiempoResolucionFormateado() : "—"
            );
        }
    }
    
}

    // ============================================================
    // EXPORTACIÓN A XLSX — usando Apache POI
    // ============================================================
    public void exportarMesActualXLSX(String ruta) throws Exception {

        List<Incidencia> lista = incidenciaService.incidenciasDelMesActual();
        List<Tecnico> tecnicos = incidenciaService.listarTecnicos();

        // Crear mapa técnico → nombre
        Map<String, String> tecMap = new java.util.HashMap<>();
        for (Tecnico t : tecnicos) {
            tecMap.put(t.getId(), t.getNombreCompleto());
        }

        // Crear libro Excel
        org.apache.poi.ss.usermodel.Workbook wb = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = wb.createSheet("Incidencias");

        // Estilo de cabecera
        org.apache.poi.ss.usermodel.CellStyle headerStyle = wb.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();

        headerFont.setBold(true);
        headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        // ============================
        // CABECERA
        // ============================
        String[] headers = {
            "ID", "Título", "Descripción", "Categoría", "Prioridad",
            "Solicitante", "Email", "Estado", "Fecha Apertura",
            "Fecha Cierre", "Técnico", "Solución", "Tiempo Resolución"
        };

        org.apache.poi.ss.usermodel.Row row0 = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = row0.createCell(i);
            c.setCellValue(headers[i]);
            c.setCellStyle(headerStyle);
        }

        // ============================
        // FILAS DE DATOS
        // ============================
        int rowIndex = 1;

        for (Incidencia i : lista) {

            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIndex++);

            String tecnico = "Sin asignar";
            if (i.getTecnicoAsignadoId() != null) {
                tecnico = tecMap.getOrDefault(i.getTecnicoAsignadoId(), "Desconocido");
            }

            row.createCell(0).setCellValue(i.getId());
            row.createCell(1).setCellValue(i.getTitulo());
            row.createCell(2).setCellValue(i.getDescripcion());
            row.createCell(3).setCellValue(i.getCategoria() != null ? i.getCategoria().name() : "—");
            row.createCell(4).setCellValue(i.getPrioridad() != null ? i.getPrioridad().name() : "—");
            row.createCell(5).setCellValue(i.getNombreSolicitante());
            row.createCell(6).setCellValue(i.getEmailSolicitante());
            row.createCell(7).setCellValue(i.getEstado() != null ? i.getEstado().name() : "—");

            row.createCell(8).setCellValue(
                    i.getFechaApertura() != null ?
                    i.getFechaApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "—"
            );

            row.createCell(9).setCellValue(
                    i.getFechaResolucion() != null ?
                    i.getFechaResolucion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "—"
            );

            row.createCell(10).setCellValue(tecnico);
            row.createCell(11).setCellValue(i.getDescripcionSolucion());
            row.createCell(12).setCellValue(
                    i.getFechaResolucion() != null ? i.getTiempoResolucionFormateado() : "—"
            );
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Guardar archivo
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(ruta)) {
            wb.write(fos);
        }

        wb.close();
    }

    // ============================================================
    // EXPORTACIÓN A PDF — Informe mensual con KPIs + tabla
    // ============================================================
    public void exportarInformeMensualPDF(String ruta) throws Exception {

        List<Incidencia> lista = incidenciaService.incidenciasDelMesActual();
        List<Tecnico> tecnicos = incidenciaService.listarTecnicos();

        // Crear mapa técnico → nombre
        Map<String, String> tecMap = new java.util.HashMap<>();
        for (Tecnico t : tecnicos) {
            tecMap.put(t.getId(), t.getNombreCompleto());
        }

        PdfWriter writer = new PdfWriter(ruta);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        DeviceRgb navy   = new DeviceRgb(26, 46, 74);
        DeviceRgb accent = new DeviceRgb(30, 144, 255);
        DeviceRgb light  = new DeviceRgb(234, 241, 248);

        // Título
        doc.add(new Paragraph("HelpDesk IT — Informe Mensual de Soporte")
                .setFontSize(20).setBold().setFontColor(navy)
                .setTextAlignment(TextAlignment.CENTER));

        doc.add(new Paragraph(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")))
                .setFontSize(12)
                .setFontColor(new DeviceRgb(100,120,140))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // ============================
        // KPIs
        // ============================
        doc.add(new Paragraph("Resumen del mes")
                .setFontSize(14).setBold().setFontColor(navy));

        Table kpi = new Table(UnitValue.createPercentArray(new float[]{1,1,1,1}))
                .useAllAvailableWidth();

        // Total incidencias
        addKPI(kpi, "Total incidencias", String.valueOf(lista.size()), accent);

        // Resueltas
        int resueltas = 0;
        for (Incidencia i : lista) {
            if (i.getEstado() == Incidencia.Estado.RESUELTA ||
                i.getEstado() == Incidencia.Estado.CERRADA) {
                resueltas++;
            }
        }
        addKPI(kpi, "Resueltas/Cerradas", String.valueOf(resueltas), accent);

        // Críticas abiertas
        int criticasAbiertas = 0;
        for (Incidencia i : lista) {
            if (i.getPrioridad() == Incidencia.Prioridad.CRITICA &&
                i.getEstado() != Incidencia.Estado.CERRADA &&
                i.getEstado() != Incidencia.Estado.RESUELTA) {
                criticasAbiertas++;
            }
        }
        addKPI(kpi, "Críticas abiertas", String.valueOf(criticasAbiertas),
                new DeviceRgb(239, 68, 68));

        // SLA críticas
        String sla = String.format("%.1f%%", incidenciaService.porcentajeSLACriticas());
        addKPI(kpi, "SLA críticas (%)", sla, accent);

        doc.add(kpi);
        doc.add(new Paragraph(" "));

        // ============================
        // TABLA DE INCIDENCIAS
        // ============================
        doc.add(new Paragraph("Listado de incidencias")
                .setFontSize(14).setBold().setFontColor(navy).setMarginTop(16));

        Table tabla = new Table(UnitValue.createPercentArray(
                new float[]{0.8f,2.5f,1,1,1,1.5f,1.2f,1.2f}))
                .useAllAvailableWidth();

        String[] headers = {"ID","Título","Categoría","Prioridad","Estado","Técnico","Apertura", "Resolución"};
        for (String h : headers) {
            tabla.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setBold().setFontSize(8).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(navy).setPadding(5));
        }

        boolean alt = false;
        for (Incidencia i : lista) {

            DeviceRgb bg = alt ? light : new DeviceRgb(255,255,255);
            alt = !alt;

            String tec = "—";
            if (i.getTecnicoAsignadoId() != null) {
                tec = tecMap.getOrDefault(i.getTecnicoAsignadoId(), "—");
            }

            tabla.addCell(cell(i.getId().substring(0,8), bg));
            tabla.addCell(cell(corta(i.getTitulo(), 30), bg));
            tabla.addCell(cell(i.getCategoria() == null ? "—" : i.getCategoria().name(), bg));
            tabla.addCell(cell(i.getPrioridad() == null ? "—" : i.getPrioridad().name(), bg));
            tabla.addCell(cell(i.getEstado() == null ? "—" : i.getEstado().name(), bg));
            tabla.addCell(cell(corta(tec, 18), bg));
            tabla.addCell(cell(i.getFechaApertura() == null ? "—" :
                    i.getFechaApertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bg));
            tabla.addCell(cell(i.getFechaResolucion() == null ? "—" :
                    i.getFechaResolucion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), bg));
        }

        doc.add(tabla);

        doc.close();
    }

    private void addKPI(Table t, String label, String value, DeviceRgb color) {
        Cell c = new Cell();
        c.add(new Paragraph(value)
                .setFontSize(22)
                .setBold()
                .setFontColor(color));
        c.add(new Paragraph(label)
                .setFontSize(9)
                .setFontColor(new DeviceRgb(100,120,140)));
        c.setPadding(12);
        t.addCell(c);
    }

    private Cell cell(String text, DeviceRgb bg) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(8))
                .setBackgroundColor(bg)
                .setPadding(4);
    }

    private String corta(String s, int max) {
        if (s == null) return "—";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private Tecnico dummy(String id) {
        Tecnico t = new Tecnico();
        t.setId(id);
        t.setNombre("Desconocido");
        t.setApellidos("");
        t.setEmailCorporativo("");
        t.setEspecialidad(Tecnico.Especialidad.USUARIO_FINAL);
        return t;
    }
}
