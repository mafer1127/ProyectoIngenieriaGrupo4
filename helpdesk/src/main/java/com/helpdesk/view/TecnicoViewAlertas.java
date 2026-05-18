package com.helpdesk.view;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.helpdesk.model.Incidencia; 

public class TecnicoViewAlertas {

    public void mostrarAlertasCriticas(List<Incidencia> criticas) {

        if (criticas.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println("INCIDENCIAS CRÍTICAS ABIERTAS MÁS DE 4 HORAS");

        for (Incidencia inc : criticas) {

            long horas = ChronoUnit.HOURS.between(
                    inc.getFechaApertura(),
                    LocalDateTime.now()
            );

            System.out.println(
                "ID: " + inc.getId() +
                " | " + inc.getTitulo() +
                " | Abierta hace " + horas + "h"
            );
        }

        System.out.println("---------------------------------------------");
    }
}
