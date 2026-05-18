package com.helpdesk;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.helpdesk.controller.IncidenciaController;
import com.helpdesk.model.Incidencia;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.persistence.IncidenciaRepository;

public class AlertasCriticasTest {

    @Test
    public void testIncidenciaCriticaDetectada() {

        // Repositorio vacío
        IncidenciaRepository repo = new IncidenciaRepository();
        IncidenciaController controller = new IncidenciaController(repo);

        // Crear incidencia crítica simulada hace 5 horas
        Incidencia inc = new Incidencia(
            1,
            "Servidor caído",
            "No responde",
            Categoria.RED,
            Prioridad.CRITICA,
            "Maria",
            "maria@test.com"
        );

        // Simular que fue creada hace 5 horas
        inc.setFechaApertura(LocalDateTime.now().minusHours(5));

        // Guardarla en el repo
        repo.agregar(inc);

        // Ejecutar la detección
        List<Incidencia> criticas = controller.obtenerIncidenciasCriticas();

        // Debe detectarse
        assertEquals(1, criticas.size());
        assertEquals(inc, criticas.get(0));
    }

    @Test
    public void testIncidenciaNoCriticaNoAparece() {

        IncidenciaRepository repo = new IncidenciaRepository();
        IncidenciaController controller = new IncidenciaController(repo);

        Incidencia inc = new Incidencia(
            2,
            "Impresora",
            "Sin tinta",
            Categoria.HARDWARE,
            Prioridad.BAJA,
            "Juan",
            "juan@test.com"
        );

        inc.setFechaApertura(LocalDateTime.now().minusHours(10));

        repo.agregar(inc);

        List<Incidencia> criticas = controller.obtenerIncidenciasCriticas();

        assertTrue(criticas.isEmpty());
    }
}
