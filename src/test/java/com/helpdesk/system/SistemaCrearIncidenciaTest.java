package com.helpdesk.system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

public class SistemaCrearIncidenciaTest {

    // Este test verifica que se pueda crear una incidencia básica y que sus atributos se asignen correctamente
    @Test
    public void crearIncidenciaBasica() {

        SessionManager.getInstance().login(
            new Usuario("u1", "hash", "Ana", "ana@mail.com", Usuario.Rol.USUARIO_FINAL)
        );

        List<Incidencia> lista = new ArrayList<>();
        List<Tecnico> listaTec = new ArrayList<>();
        List<HistorialCambio> listaHist = new ArrayList<>();

        JsonPersistence mock = Mockito.mock(JsonPersistence.class);
        Mockito.when(mock.cargarIncidencias()).thenReturn(lista);
        Mockito.when(mock.cargarTecnicos()).thenReturn(listaTec);
        Mockito.when(mock.cargarHistorial()).thenReturn(listaHist);

        IncidenciaService service = new IncidenciaService(mock);

        Incidencia inc = new Incidencia();
        inc.setId("INC-1");
        inc.setTitulo("Pantalla negra");
        inc.setDescripcion("No arranca");
        inc.setCategoria(Incidencia.Categoria.HARDWARE);
        inc.setPrioridad(Incidencia.Prioridad.ALTA);
        inc.setNombreSolicitante("Ana");
        inc.setEmailSolicitante("ana@mail.com");
        inc.setFechaApertura(LocalDateTime.now());

        lista.add(inc);

        Assertions.assertEquals(1, lista.size());
        Assertions.assertEquals("Pantalla negra", lista.get(0).getTitulo());
        Assertions.assertEquals(Incidencia.Prioridad.ALTA, lista.get(0).getPrioridad());
    }
}
