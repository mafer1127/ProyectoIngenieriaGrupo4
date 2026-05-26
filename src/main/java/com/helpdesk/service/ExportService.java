package com.helpdesk.service;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;

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
}
