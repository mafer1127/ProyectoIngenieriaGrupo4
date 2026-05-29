package com.helpdesk.system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.helpdesk.model.HistorialCambio;
import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.model.Usuario;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;

public class SistemaKPIsPrioridadTest {

    // Este test verifica que el método resumenAbiertasPorPrioridad funcione correctamente
    @Test
    public void resumenPorPrioridad_funciona() {

        // Login como técnico (rol válido)
        SessionManager.getInstance().login(
            new Usuario("tec1", "hash", "Carlos", "carlos@mail.com", Usuario.Rol.TECNICO)
        );

        List<Incidencia> listaInc = new ArrayList<>();
        List<Tecnico> listaTec = new ArrayList<>();
        List<HistorialCambio> listaHist = new ArrayList<>();

        JsonPersistence mock = Mockito.mock(JsonPersistence.class);
        Mockito.when(mock.cargarIncidencias()).thenReturn(listaInc);
        Mockito.when(mock.cargarTecnicos()).thenReturn(listaTec);
        Mockito.when(mock.cargarHistorial()).thenReturn(listaHist);

        IncidenciaService service = new IncidenciaService(mock);

        // Incidencia 1
        Incidencia a = new Incidencia();
        a.setId("A");
        a.setTitulo("Error login");
        a.setPrioridad(Incidencia.Prioridad.ALTA);
        a.setEstado(Incidencia.Estado.ABIERTA);
        a.setFechaApertura(LocalDateTime.now());
        listaInc.add(a);

        // Incidencia 2
        Incidencia b = new Incidencia();
        b.setId("B");
        b.setTitulo("Teclado roto");
        b.setPrioridad(Incidencia.Prioridad.BAJA);
        b.setEstado(Incidencia.Estado.EN_CURSO);
        b.setFechaApertura(LocalDateTime.now());
        listaInc.add(b);

        Map<Incidencia.Prioridad, Integer> resumen = service.resumenAbiertasPorPrioridad();

        Assertions.assertEquals(1, resumen.get(Incidencia.Prioridad.ALTA));
        Assertions.assertEquals(1, resumen.get(Incidencia.Prioridad.BAJA));
    }
}
