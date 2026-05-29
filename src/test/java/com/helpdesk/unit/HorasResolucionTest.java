package com.helpdesk.unit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.helpdesk.model.Incidencia;

public class HorasResolucionTest {

    
//   Validar que el método getHorasResolucion() calcula correctamente la diferencia entre fechaApertura y fechaResolucion usando Duration.between()
    @Test
    void calculaHorasCorrectamente() {
        Incidencia inc = new Incidencia();
        inc.setFechaApertura(LocalDateTime.of(2024, 1, 1, 10, 0));
        inc.setFechaResolucion(LocalDateTime.of(2024, 1, 1, 15, 0));

        long horas = inc.getHorasResolucion();

        assertEquals(5, horas);
    }
}
