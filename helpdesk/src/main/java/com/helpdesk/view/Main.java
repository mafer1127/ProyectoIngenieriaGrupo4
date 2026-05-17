package com.helpdesk.view;

import java.util.Scanner;

import com.helpdesk.auth.AuthService;
import com.helpdesk.auth.Rol;
import com.helpdesk.auth.User;
import com.helpdesk.controller.IncidenciaController;
import com.helpdesk.controller.TecnicoController;
import com.helpdesk.exceptions.TecnicoNoDisponibleException;
import com.helpdesk.exceptions.ValidacionDatosException;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Especialidad;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.persistence.IncidenciaRepository;
import com.helpdesk.persistence.TecnicoRepository;
import com.helpdesk.utils.Utils;
import com.helpdesk.utils.UtilsAuth;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        AuthService auth = new AuthService();

        // ============================
        // MENÚ INICIAL LOGIN / SIGNUP
        // ============================
        System.out.println("=== SISTEMA HELP DESK ===");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("0. Salir");
        System.out.print("Opción: ");

        int inicio = leerOpcion(sc);
        User logged = null;

        switch (inicio) {
            case 1:
                logged = UtilsAuth.loginSeguro(sc, auth);
                break;

            case 2:
                signUp(sc, auth);
                logged = UtilsAuth.loginSeguro(sc, auth);
                break;

            case 0:
                System.out.println("Saliendo...");
                return;

            default:
                System.out.println("Opción no válida");
                return;
        }

        if (logged == null) {
            System.out.println("No se pudo iniciar sesión. Saliendo...");
            return;
        }

        System.out.println("Bienvenido, rol: " + logged.getRol());

        boolean esTecnico = logged.getRol() == Rol.TECNICO;



        IncidenciaRepository repo = new IncidenciaRepository();
        IncidenciaController controller = new IncidenciaController(repo);

        TecnicoRepository repoTec = new TecnicoRepository();
        TecnicoController tecnicoController = new TecnicoController(repoTec);


        // ============================
        // MENÚ PRINCIPAL
        // ============================

        int opcion;

        do {
            System.out.println("\n=== SISTEMA HELP DESK ===");

            if (esTecnico) {
                System.out.println("1. Carga de datos");
            }

            System.out.println("2. Incidencias");

            if (esTecnico) {
                System.out.println("3. Técnicos");
                System.out.println("4. Estadísticas");
            }

            System.out.println("5. Historial");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            opcion = leerOpcion(sc);

            switch (opcion) {

                case 1:
                    if (esTecnico) menuCargaDatos(sc);
                    else System.out.println("Opción no válida");
                    break;

                case 2:
                    menuIncidencias(sc, controller, tecnicoController, esTecnico);
                    break;

                case 3:
                    if (esTecnico) menuTecnicos(sc, tecnicoController);
                    else System.out.println("Opción no válida");
                    break;

                case 4:
                    if (esTecnico) menuEstadisticas(sc);
                    else System.out.println("Opción no válida");
                    break;

                case 5:
                    //menuHistorial(sc);
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

    // ============================
    // SIGN UP
    // ============================
    private static void signUp(Scanner sc, AuthService auth) {

        System.out.println("\n=== REGISTRO ===");

        String username = UtilsAuth.leerUsernameValido(sc);

        if (auth.exists(username)) {
            System.out.println("Ese usuario ya existe.");
            return;
        }

        String password = UtilsAuth.leerPasswordSegura(sc);

        int tipo = UtilsAuth.leerOpcionRol(sc);

        Rol rol = (tipo == 2) ? Rol.TECNICO : Rol.USUARIO;

        auth.register(username, password, rol);

        System.out.println("Registro exitoso. Ahora puedes iniciar sesión");
    }

    // ============================
    // LECTURA SEGURA DE OPCIONES
    // ============================

    private static int leerOpcion(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Opción no válida. Ingresa un número válido");
            sc.nextLine();
            System.out.print("Opción: ");
        }
        int op = sc.nextInt();
        sc.nextLine();
        return op;
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

            op = leerOpcion(sc);

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

    private static void menuIncidencias(Scanner sc, IncidenciaController controller, TecnicoController tecnicoController, boolean esTecnico) {

    int op;

    do {
        System.out.println("\n--- Gestión de Incidencias ---");
        System.out.println("1. Registrar incidencia");
        System.out.println("2. Consultar incidencia");
        System.out.println("3. Listar incidencias");
        System.out.println("4. Editar incidencia");
        System.out.println("5. Eliminar incidencia");

        if (esTecnico) {
            System.out.println("6. Asignar incidencia a técnico");
        }

        System.out.println("0. Volver");
        System.out.print("Opción: ");

        op = leerOpcion(sc);

        switch (op) {

            case 1:
                System.out.print("Título: ");
                String titulo = sc.nextLine();

                System.out.print("Descripción: ");
                String descripcion = sc.nextLine();

                Categoria categoria = Utils.leerEnumValido(sc, "Categoría (HARDWARE/SOFTWARE/RED/ACCESO/OTRO): ", Categoria.class);

                Prioridad prioridad = Utils.leerEnumValido(sc, "Prioridad (BAJA/MEDIA/ALTA/CRITICA): ", Prioridad.class);

                String solicitante = Utils.leerNombreValido(sc, "Nombre del solicitante: ");

                String emailCliente = Utils.leerEmailValido(sc, "Email: ");

                controller.crearIncidencia(titulo, descripcion, categoria, prioridad, solicitante, emailCliente);
                break;

            case 2:
                int idConsulta = Utils.leerIdPositivo(sc, "ID de la incidencia a consultar: ");
                controller.consultarIncidencia(idConsulta);
                break;

            case 3:
                controller.listarIncidencias();
                break;

            case 4:
                int idEdit = Utils.leerIdPositivo(sc, "ID de la incidencia a editar: ");
                controller.editarIncidencia(idEdit, sc);
                break;

            case 5:
                int idEliminar = Utils.leerIdPositivo(sc, "ID de la incidencia a eliminar: ");
                controller.eliminarIncidencia(idEliminar);
                break;

            case 6:
                if (!esTecnico) {
                    System.out.println("Opción no válida");
                    break;
                }

                int idInc = Utils.leerIdPositivo(sc, "ID de incidencia: ");

                try {
                    controller.asignarTecnico(idInc, sc, tecnicoController);
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

            op = leerOpcion(sc);
            

            try {

                switch (op) {

                    case 1:
                    String nombre = Utils.leerNombreValido(sc, "Nombre: ");

                    String apellidos = Utils.leerNombreValido(sc, "Apellidos: ");

                    String email = Utils.leerEmailValido(sc, "Ingrese el email: ");

                    Especialidad esp = Utils.leerEnumValido(sc, "Especialidad (SISTEMAS/REDES/USUARIO_FINAL/SEGURIDAD): ", Especialidad.class);

                    tecnicoController.crearTecnico(nombre, apellidos, email, esp);
                    break;
    
                    case 2:
                        int idAct = Utils.leerIdPositivo(sc, "ID del técnico: ");
                        tecnicoController.activar(idAct);
                        break;

                    case 3:
                        int idDes = Utils.leerIdPositivo(sc, "ID del técnico: ");
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

            op = leerOpcion(sc);

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

}
