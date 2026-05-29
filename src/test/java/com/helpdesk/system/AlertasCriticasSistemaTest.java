package com.helpdesk.system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Usuario;
import com.helpdesk.persistence.JsonPersistence;
import com.helpdesk.service.IncidenciaService;
import com.helpdesk.service.SessionManager;

public class AlertasCriticasSistemaTest {

    // Este test verifica que el sistema detecta correctamente las incidencias críticas que han estado abiertas por más de 4 horas
    @Test
    public void detectarCriticasFueraSLA() {

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

        List<Incidencia> lista = new ArrayList<>();

        JsonPersistence mock = Mockito.mock(JsonPersistence.class);
        Mockito.when(mock.cargarIncidencias()).thenReturn(lista);

        IncidenciaService service = new IncidenciaService(mock);

        Incidencia i = new Incidencia();
        i.setId("INC-999");
        i.setTitulo("Servidor caído");
        i.setDescripcion("No responde el servidor principal");
        i.setCategoria(Incidencia.Categoria.RED);
        i.setPrioridad(Incidencia.Prioridad.CRITICA);
        i.setNombreSolicitante("Admin");
        i.setEmailSolicitante("admin@mail.com");
        i.setFechaApertura(LocalDateTime.now().minusHours(6));
        i.setEstado(Incidencia.Estado.ABIERTA);
        lista.add(i);

        List<Incidencia> criticas = service.alertasCriticas();

        Assertions.assertEquals(1, criticas.size());
        Assertions.assertEquals("INC-999", criticas.get(0).getId());
    }

}
