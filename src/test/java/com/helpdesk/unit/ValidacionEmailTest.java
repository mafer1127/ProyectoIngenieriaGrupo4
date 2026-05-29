package com.helpdesk.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.helpdesk.model.Incidencia;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;

public class ValidacionEmailTest {

    //Verificar que el método crearIncidencia() rechaza correos inválidos
    @Test
    void emailInvalidoDebeLanzarExcepcion() {
        JsonPersistence mock = Mockito.mock(JsonPersistence.class);
        IncidenciaService service = new IncidenciaService(mock);

        Incidencia inc = new Incidencia();
        inc.setTitulo("Prueba");
        inc.setDescripcion("Desc");
        inc.setCategoria(Incidencia.Categoria.SOFTWARE);
        inc.setPrioridad(Incidencia.Prioridad.MEDIA);
        inc.setNombreSolicitante("María");
        inc.setEmailSolicitante("correo-malo");

        assertThrows(IllegalArgumentException.class, () -> {
            service.crearIncidencia(inc);
        });
    }
}
