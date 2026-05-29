package com.helpdesk.integration;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helpdesk.model.Incidencia;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;

public class CrearIncidenciaIntegracionTest {

    //   Comprobar que crearIncidencia(), valida la incidencia, la añade a la lista interna, registra historial y llama a guardarIncidencias() en JsonPersistence
    @Test
    void crearIncidenciaDebeGuardarEnJSON() {
        JsonPersistence mock = mock(JsonPersistence.class);

        // Simular carga inicial vacía
        when(mock.cargarIncidencias()).thenReturn(new ArrayList<>());
        when(mock.cargarTecnicos()).thenReturn(new ArrayList<>());
        when(mock.cargarHistorial()).thenReturn(new ArrayList<>());

        IncidenciaService service = new IncidenciaService(mock);

        Incidencia inc = new Incidencia();
        inc.setId("inc1");
        inc.setTitulo("Error PC");
        inc.setDescripcion("Pantalla azul");
        inc.setCategoria(Incidencia.Categoria.HARDWARE);
        inc.setPrioridad(Incidencia.Prioridad.ALTA);
        inc.setNombreSolicitante("María");
        inc.setEmailSolicitante("maria@test.com");

        inc.setFechaApertura(LocalDateTime.now());

        service.crearIncidencia(inc);

        // Verificar persistencia
        verify(mock, times(1)).guardarIncidencias(anyList());
        verify(mock, times(1)).guardarHistorial(anyList());
    }
}
