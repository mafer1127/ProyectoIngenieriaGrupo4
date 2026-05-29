package com.helpdesk.integration;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.helpdesk.model.Incidencia;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.ExportService;
import com.helpdesk.service.IncidenciaService;

public class ExportarCSVIntegracionTest {

    //   Verificar que exportarMesActualCSV(), obtiene las incidencias del mes actual, genera un archivo CSV real y escribe encabezados y filas correctamente
    @Test
    void exportarMesActualDebeGenerarCSV() throws Exception {
        JsonPersistence mock = mock(JsonPersistence.class);

        Incidencia inc = new Incidencia();
        inc.setId("inc1");
        inc.setTitulo("Prueba");
        inc.setFechaApertura(LocalDateTime.now());

        when(mock.cargarIncidencias()).thenReturn(List.of(inc));
        when(mock.cargarTecnicos()).thenReturn(List.of());
        when(mock.cargarHistorial()).thenReturn(List.of());

        IncidenciaService service = new IncidenciaService(mock);
        ExportService export = new ExportService(service);

        File temp = File.createTempFile("test", ".csv");

        export.exportarMesActualCSV(temp.getAbsolutePath());

        String contenido = Files.readString(temp.toPath());

        assert contenido.contains("ID");
        assert contenido.contains("inc1");
    }
}
