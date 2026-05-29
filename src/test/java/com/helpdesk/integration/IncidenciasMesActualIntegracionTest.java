package com.helpdesk.integration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.helpdesk.model.Incidencia;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;

public class IncidenciasMesActualIntegracionTest {

    //   Verificar que incidenciasDelMesActual() devuelve únicamente las incidencias cuya fecha de apertura pertenece al mes actual
    @Test
    void debeFiltrarIncidenciasDelMesActual() {
        JsonPersistence mock = mock(JsonPersistence.class);

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime mesPasado = ahora.minusMonths(1);

        Incidencia inc1 = new Incidencia();
        inc1.setId("1");
        inc1.setTitulo("A");
        inc1.setDescripcion("A");
        inc1.setCategoria(Incidencia.Categoria.HARDWARE);
        inc1.setPrioridad(Incidencia.Prioridad.ALTA);
        inc1.setNombreSolicitante("María");
        inc1.setEmailSolicitante("maria@test.com");
        inc1.setFechaApertura(ahora);

        Incidencia inc2 = new Incidencia();
        inc2.setId("2");
        inc2.setTitulo("B");
        inc2.setDescripcion("B");
        inc2.setCategoria(Incidencia.Categoria.SOFTWARE);
        inc2.setPrioridad(Incidencia.Prioridad.BAJA);
        inc2.setNombreSolicitante("Juan");
        inc2.setEmailSolicitante("juan@test.com");
        inc2.setFechaApertura(mesPasado);

        when(mock.cargarIncidencias()).thenReturn(new ArrayList<>(List.of(inc1, inc2)));
        when(mock.cargarTecnicos()).thenReturn(new ArrayList<>());
        when(mock.cargarHistorial()).thenReturn(new ArrayList<>());

        IncidenciaService service = new IncidenciaService(mock);

        List<Incidencia> delMes = service.incidenciasDelMesActual();

        assertEquals(1, delMes.size());
        assertEquals("1", delMes.get(0).getId());
    }
}

