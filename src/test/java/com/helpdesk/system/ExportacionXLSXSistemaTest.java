package com.helpdesk.system;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Usuario;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.ExportService;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;

public class ExportacionXLSXSistemaTest {

    // Este test verifica que el sistema puede exportar correctamente las incidencias del mes actual a un archivo XLSX
    @Test
    public void exportarIncidenciasMesActualXLSX() throws Exception {

        // Simular login obligatorio
        SessionManager.getInstance().login(
            new Usuario(
                "tec1",                // username
                "hash",                // passwordHash
                "Carlos García",       // nombreCompleto
                "carlos@mail.com",     // email
                Usuario.Rol.TECNICO    // rol
            )
        );

        // Lista simulada de incidencias
        List<Incidencia> lista = new ArrayList<>();

        JsonPersistence mock = Mockito.mock(JsonPersistence.class);
        Mockito.when(mock.cargarIncidencias()).thenReturn(lista);

        IncidenciaService service = new IncidenciaService(mock);
        ExportService export = new ExportService(service);

        // Incidencia del mes actual
        Incidencia i = new Incidencia();
        i.setId("INC-300");
        i.setTitulo("Fallo en VPN");
        i.setDescripcion("No conecta la VPN corporativa");
        i.setCategoria(Incidencia.Categoria.RED);
        i.setPrioridad(Incidencia.Prioridad.ALTA);
        i.setNombreSolicitante("Laura");
        i.setEmailSolicitante("laura@mail.com");
        i.setFechaApertura(LocalDateTime.now());
        i.setEstado(Incidencia.Estado.ABIERTA);
        lista.add(i);

        // Ruta temporal
        String ruta = "test_export.xlsx";

        // Ejecutar exportación
        export.exportarMesActualXLSX(ruta);

        // Verificar que el archivo existe
        File archivo = new File(ruta);
        Assertions.assertTrue(archivo.exists(), "El archivo XLSX no fue creado");

        // Verificar contenido básico del XLSX
        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook wb = WorkbookFactory.create(fis)) {

            Assertions.assertTrue(wb.getNumberOfSheets() > 0, "El XLSX no tiene hojas");

            var sheet = wb.getSheetAt(0);
            Assertions.assertTrue(sheet.getPhysicalNumberOfRows() > 1,
                    "El XLSX no contiene datos (solo encabezados)");
        }

        // Limpieza
        archivo.delete();
    }
}
