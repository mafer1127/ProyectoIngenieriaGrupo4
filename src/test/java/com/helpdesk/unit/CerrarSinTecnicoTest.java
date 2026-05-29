package com.helpdesk.unit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Incidencia.Estado;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;

public class CerrarSinTecnicoTest {

    //   Verificar que cambiarEstado() no permite pasar una incidencia a RESUELTA si no tiene un técnico asignado
    @Test
    public void noDebeCerrarIncidenciaSinTecnico() {
        JsonPersistence mock = Mockito.mock(JsonPersistence.class);

        // Crear incidencia
        Incidencia inc = new Incidencia();
        inc.setId("inc1");

        // Mockear carga inicial
        Mockito.when(mock.cargarIncidencias()).thenReturn(List.of(inc));
        Mockito.when(mock.cargarTecnicos()).thenReturn(List.of());

        IncidenciaService service = new IncidenciaService(mock);

        assertThrows(IllegalStateException.class, () -> {
            service.cambiarEstado("inc1", Estado.RESUELTA, "Intento de cierre");
        });
    }
}
