package com.helpdesk.view;

import java.util.Scanner;

import com.helpdesk.controller.IncidenciaController;
import com.helpdesk.controller.TecnicoController;
import com.helpdesk.exceptions.TecnicoNoDisponibleException;
import com.helpdesk.exceptions.ValidacionDatosException;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.persistence.IncidenciaRepository;
import com.helpdesk.persistence.TecnicoRepository;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);


        IncidenciaRepository repo = new IncidenciaRepository();
        IncidenciaController controller = new IncidenciaController(repo);

        TecnicoRepository repoTec = new TecnicoRepository();
        TecnicoController tecnicoController = new TecnicoController(repoTec);

        int opcion;

        do {
            System.out.println("\n=== SISTEMA HELP DESK ===");
            System.out.println("1. Carga de datos");
            System.out.println("2. Incidencias");
            System.out.println("3. Técnicos");
            System.out.println("4. Estadísticas");
            System.out.println("5. Configuración");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {

                case 1:
                    menuCargaDatos(sc);
                    break;

                case 2:
                    menuIncidencias(sc, controller, tecnicoController);
                    break;

                case 3:
                    menuTecnicos(sc, tecnicoController);
                    break;

                case 4:
                    menuEstadisticas(sc);
                    break;

                case 5:
                    menuConfiguracion(sc);
                    break;

                case 0:
                    System.out.println("Saliendo del sistema...");
                    break;

                default:
                    System.out.println("Opción no válida");
            }

        } while (opcion != 0);

        sc.close();
    }

    
    // ============================================================
    // SUBMENÚ: CARGA DE DATOS 
    // ============================================================

    private static void menuCargaDatos(Scanner sc) {
        int op;
        do {
            System.out.println("\n--- Carga de Datos (JSON) ---");
            System.out.println("1. Guardar datos en JSON");
            System.out.println("2. Cargar datos desde JSON");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    System.out.println("Función guardar JSON (pendiente)");
                    break;
                case 2:
                    System.out.println("Función cargar JSON (pendiente)");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida");
            }

        } while (op != 0);
    }

    // ============================================================
    // SUBMENÚ: INCIDENCIAS 
    // ============================================================

    private static void menuIncidencias(Scanner sc, IncidenciaController controller, TecnicoController tecnicoController) {

        int op;

        do {
            System.out.println("\n--- Gestión de Incidencias ---");
            System.out.println("1. Registrar incidencia");
            System.out.println("2. Consultar incidencia");
            System.out.println("3. Listar incidencias");
            System.out.println("4. Editar incidencia");
            System.out.println("5. Eliminar incidencia");
            System.out.println("6. Asignar incidencia a técnico");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            op = sc.nextInt();
            sc.nextLine();

            switch (op) {

                case 1:
                    System.out.print("Título: ");
                    String titulo = sc.nextLine();

                    System.out.print("Descripción: ");
                    String descripcion = sc.nextLine();

                    System.out.print("Categoría (HARDWARE/SOFTWARE/RED/ACCESO/OTRO): ");
                    Categoria categoria = Categoria.valueOf(sc.nextLine().toUpperCase());

                    System.out.print("Prioridad (BAJA/MEDIA/ALTA/CRITICA): ");
                    Prioridad prioridad = Prioridad.valueOf(sc.nextLine().toUpperCase());

                    System.out.print("Solicitante: ");
                    String solicitante = sc.nextLine();

                    System.out.print("Email solicitante: ");
                    String email = sc.nextLine();

                    controller.crearIncidencia(titulo, descripcion, categoria, prioridad, solicitante, email);
                    break;

                case 2:
                    System.out.print("ID: ");
                    int idConsulta = sc.nextInt();
                    controller.consultarIncidencia(idConsulta);
                    break;

                case 3:
                    controller.listarIncidencias();
                    break;

                case 4:
                    System.out.print("ID: ");
                    int idEdit = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Nuevo título: ");
                    String nuevoTitulo = sc.nextLine();

                    System.out.print("Nueva descripción: ");
                    String nuevaDesc = sc.nextLine();

                    controller.editarIncidencia(idEdit, nuevoTitulo, nuevaDesc);
                    break;

                case 5:
                    System.out.print("ID: ");
                    int idDel = sc.nextInt();
                    controller.eliminarIncidencia(idDel);
                    break;
                case 6:
                    System.out.print("ID de incidencia: ");
                    int idInc = sc.nextInt();

                    System.out.print("ID de técnico: ");
                    int idTec = sc.nextInt();

                    try {
                        controller.asignarTecnico(idInc, idTec, tecnicoController);
                    } catch (ValidacionDatosException | TecnicoNoDisponibleException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Opción no válida");
            }

        } while (op != 0);
    }

    // ============================================================
    // SUBMENÚ: TÉCNICOS
    // ============================================================

    private static void menuTecnicos(Scanner sc, TecnicoController tecnicoController) {

    int op;

        do {
            System.out.println("\n--- Gestión de Técnicos ---");
            System.out.println("1. Registrar técnico");
            System.out.println("2. Activar técnico");
            System.out.println("3. Desactivar técnico");
            System.out.println("4. Listar técnicos");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            op = sc.nextInt();
            sc.nextLine();

            try {

                switch (op) {

                    case 1:
                        System.out.print("Nombre: ");
                        String nombre = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        tecnicoController.crearTecnico(nombre, email);
                        break;

                    case 2:
                        System.out.print("ID del técnico: ");
                        int idAct = sc.nextInt();
                        tecnicoController.activar(idAct);
                        break;

                    case 3:
                        System.out.print("ID del técnico: ");
                        int idDes = sc.nextInt();
                        tecnicoController.desactivar(idDes);
                        break;

                    case 4:
                        tecnicoController.listarTecnicos();
                        break;

                    case 0:
                        System.out.println("Volviendo...");
                        break;

                    default:
                        System.out.println("Opción no válida");
                }

            } catch (ValidacionDatosException | TecnicoNoDisponibleException e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (op != 0);
    }

    // ============================================================
    // SUBMENÚ: ESTADÍSTICAS
    // ============================================================

    private static void menuEstadisticas(Scanner sc) {
        int op;
        do {
            System.out.println("\n--- Estadísticas ---");
            System.out.println("1. Incidencias por categoría");
            System.out.println("2. Incidencias por prioridad");
            System.out.println("3. Incidencias por estado");
            System.out.println("4. Tiempo promedio de resolución");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                case 2:
                case 3:
                case 4:
                    System.out.println("Estadística pendiente");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida");
            }

        } while (op != 0);
    }

    // ============================================================
    // SUBMENÚ: CONFIGURACIÓN
    // ============================================================

    private static void menuConfiguracion(Scanner sc) {
        int op;
        do {
            System.out.println("\n--- Configuración ---");
            System.out.println("1. Notificaciones (RF8)");
            System.out.println("2. Historial de cambios (RF13)");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    System.out.println("Notificaciones (pendiente)");
                    break;
                case 2:
                    System.out.println("Historial de cambios (pendiente)");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida");
            }

        } while (op != 0);
    }
}
