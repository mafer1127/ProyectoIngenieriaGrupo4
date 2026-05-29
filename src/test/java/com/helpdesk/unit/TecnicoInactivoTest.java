package com.helpdesk.unit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;

public class TecnicoInactivoTest {

    //   Comprobar que asignarTecnico() lanza IllegalArgumentException cuando el técnico tiene activo = false
    @Test
    void noDebeAsignarTecnicoInactivo() {
        JsonPersistence mock = Mockito.mock(JsonPersistence.class);

        Mockito.when(mock.cargarTecnicos()).thenReturn(List.of());
        Mockito.when(mock.cargarIncidencias()).thenReturn(List.of());

        IncidenciaService service = new IncidenciaService(mock);

        Tecnico t = new Tecnico("idTec", "Juan", "Pérez", Tecnico.Especialidad.SISTEMAS);
        t.setActivo(false);

        Incidencia inc = new Incidencia();
        inc.setId("idInc");

        service.listarTecnicos().add(t);
        service.listarIncidencias().add(inc);

        assertThrows(IllegalArgumentException.class, () -> {
            service.asignarTecnico("idInc", "idTec");
        });
    }
}
