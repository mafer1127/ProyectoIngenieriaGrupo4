package com.helpdesk.view;

import java.util.Scanner;

import com.helpdesk.controller.IncidenciaController;
import com.helpdesk.controller.TecnicoController;
import com.helpdesk.model.Incidencia;
import com.helpdesk.model.RegistroHistorial;
import com.helpdesk.utils.Utils;

public class IncidenciaView {

    // ============================================================
    // MOSTRAR HISTORIAL DE CAMBIOS
    // ============================================================
    public void mostrarHistorial(Scanner sc, IncidenciaController controller) {

        System.out.println("\n--- Historial de Cambios ---");

        int id = Utils.leerIdPositivo(sc, "ID de la incidencia: ");
        Incidencia inc = controller.getRepo().buscarPorId(id);

        if (inc == null) {
            System.out.println("Incidencia no encontrada");
            return;
        }

        if (inc.getHistorial().isEmpty()) {
            System.out.println("No hay cambios registrados para esta incidencia");
            return;
        }

        System.out.println("\nHistorial de la incidencia " + id + ":");

        for (RegistroHistorial c : inc.getHistorial()) {
            System.out.println("  " + c);
        }
    }

    // ============================================================
    // MENÚ DE LISTADO DE INCIDENCIAS
    // ============================================================
    public void menuListarIncidencias(Scanner sc, IncidenciaController controller, TecnicoController tecnicoController) {

        int op;

        do {
            System.out.println("\n--- Listado de Incidencias ---");
            System.out.println("1. Listar todas");
            System.out.println("2. Listar con filtros");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            op = Utils.leerOpcion(sc);

            switch (op) {

                case 1:
                    controller.listarIncidencias();
                    break;

                case 2:
                    controller.filtrarIncidencias(sc, tecnicoController);
                    break;

                case 0:
                    System.out.println("Volviendo...");
                    break;

                default:
                    System.out.println("Opción no válida. Intenta de nuevo");
            }

        } while (op != 0);
    }
}
