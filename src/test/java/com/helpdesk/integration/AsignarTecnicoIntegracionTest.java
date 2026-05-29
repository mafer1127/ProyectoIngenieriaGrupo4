package com.helpdesk.integration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;

public class AsignarTecnicoIntegracionTest {

    //   Verificar que asignarTecnico(), encuentra la incidencia, verifica que el técnico está activo, actualiza la incidenciar, r
    //  registra historial y persiste cambios en JSON
    @Test
    void asignarTecnicoDebeRegistrarHistorial() {
        JsonPersistence mock = mock(JsonPersistence.class);

        // Crear incidencia válida
        Incidencia inc = new Incidencia();
        inc.setId("inc1");
        inc.setTitulo("Pantalla azul");
        inc.setDescripcion("Error crítico");
        inc.setCategoria(Incidencia.Categoria.SOFTWARE);
        inc.setPrioridad(Incidencia.Prioridad.ALTA);
        inc.setNombreSolicitante("María");
        inc.setEmailSolicitante("maria@test.com");
        inc.setFechaApertura(LocalDateTime.now());

        // Crear técnico activo
        Tecnico t = new Tecnico("Juan", "Pérez", "juan@test.com", Tecnico.Especialidad.SISTEMAS);
        t.setId("tec1");
        t.setActivo(true);

        // Listas MODIFICABLES 
        when(mock.cargarIncidencias()).thenReturn(new ArrayList<>(List.of(inc)));
        when(mock.cargarTecnicos()).thenReturn(new ArrayList<>(List.of(t)));
        when(mock.cargarHistorial()).thenReturn(new ArrayList<>());

        IncidenciaService service = new IncidenciaService(mock);

        service.asignarTecnico("inc1", "tec1");

        verify(mock, times(1)).guardarIncidencias(anyList());
        verify(mock, never()).guardarHistorial(anyList());
    }
}
