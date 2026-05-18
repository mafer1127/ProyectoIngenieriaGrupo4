package com.helpdesk.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.helpdesk.exceptions.TecnicoNoDisponibleException;
import com.helpdesk.exceptions.ValidacionDatosException;
import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Estado;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.persistence.IncidenciaRepository;
import com.helpdesk.utils.Utils;

public class IncidenciaController {

    private IncidenciaRepository repo;

    public IncidenciaController(IncidenciaRepository repo) {
        this.repo = repo;
    }

    // Crear Incidencia
    public void crearIncidencia(String titulo, String descripcion, Categoria categoria,
                                Prioridad prioridad, String solicitante, String email) {

        int id = repo.generarId();
        Incidencia nueva = new Incidencia(id, titulo, descripcion, categoria, prioridad, solicitante, email);
        repo.agregar(nueva);

        System.out.println("Incidencia creada con ID: " + id);
    }

    // Consultar Incidencia
    public void consultarIncidencia(int id) {
        Incidencia i = repo.buscarPorId(id);
        if (i == null) {
            System.out.println("No existe una incidencia con ese ID");
        } else {
            System.out.println(i);
        }
    }

    // Listar Incidencias
    public void listarIncidencias() {
        if (repo.obtenerTodas().isEmpty()) {
            System.out.println("No hay incidencias registradas");
            return;
        }

        for (Incidencia i : repo.obtenerTodas()) {
            System.out.println(i);
        }
    }

    // Editar Incidencia
    public void editarIncidencia(int id, Scanner sc) {
        Incidencia i = repo.buscarPorId(id);
        if (i == null) {
            System.out.println("Incidencia no encontrada");
            return;
        }

        if (i.getEstado() == Estado.CERRADA) {
            System.out.println("No se puede editar una incidencia cerrada");
            return;
        }

        System.out.print("Nuevo título: ");
        String nuevoTitulo = sc.nextLine();

        System.out.print("Nueva descripción: ");
        String nuevaDescripcion = sc.nextLine();

        i.setTitulo(nuevoTitulo);
        i.setDescripcion(nuevaDescripcion);

        System.out.println("Incidencia actualizada");
    }

    // Eliminar Incidencia
    public void eliminarIncidencia(int id) {
        boolean eliminada = repo.eliminar(id);
        if (eliminada) {
            System.out.println("Incidencia eliminada");
        } else {
            System.out.println("No existe una incidencia con ese ID");
        }
    }

    // Asignar Técnico
    public void asignarTecnico(int idIncidencia, Scanner sc, TecnicoController tecnicoController)
            throws ValidacionDatosException, TecnicoNoDisponibleException {

        Incidencia inc = repo.buscarPorId(idIncidencia);
        if (inc == null) {
            throw new ValidacionDatosException("La incidencia no existe");
        }

        int idTecnico = Utils.leerIdPositivo(sc, "ID del técnico: ");
        Tecnico t = tecnicoController.buscar(idTecnico);

        if (t == null) {
            throw new ValidacionDatosException("El técnico no existe");
        }

        if (!t.isActivo()) {
            throw new TecnicoNoDisponibleException("No se puede asignar una incidencia a un técnico inactivo");
        }

        inc.setTecnicoAsignado(t);
        inc.registrarCambioEstado(Estado.EN_CURSO, t.getNombre());

        System.out.println("Incidencia " + idIncidencia + " asignada a "
                + t.getNombre() + " " + t.getApellidos()
                + " y ahora está EN_CURSO");
    }

    public IncidenciaRepository getRepo() { return repo;}

    // Cambiar estado (incluye resuelta y cerrada)
    public void cambiarEstado(int idIncidencia, Scanner sc, TecnicoController tecnicoController) {

        Incidencia inc = repo.buscarPorId(idIncidencia);
        if (inc == null) {
            System.out.println("Incidencia no encontrada");
            return;
        }

        if (inc.getTecnicoAsignado() == null) {
            System.out.println("La incidencia no tiene técnico asignado");
            return;
        }

        if (inc.getEstado() == Estado.CERRADA) {
            System.out.println("La incidencia ya está cerrada");
            return;
        }

        int idTecnico = Utils.leerIdPositivo(sc, "ID del técnico: ");
        Tecnico t = tecnicoController.buscar(idTecnico);

        if (t == null) {
            System.out.println("Técnico no encontrado");
            return;
        }

        if (inc.getTecnicoAsignado().getId() != idTecnico) {
            System.out.println("Solo el técnico asignado puede cambiar el estado");
            return;
        }

        Estado actual = inc.getEstado();
        System.out.println("Estado actual: " + actual);

        System.out.println("\nEstados disponibles:");

        if (actual == Estado.EN_CURSO) {
            System.out.println("1. EN_ESPERA");
            System.out.println("2. RESUELTA");
            System.out.println("3. CERRADA");
        } else if (actual == Estado.EN_ESPERA) {
            System.out.println("1. EN_CURSO");
            System.out.println("2. RESUELTA");
            System.out.println("3. CERRADA");
        } else if (actual == Estado.RESUELTA) {
            System.out.println("1. CERRADA");
        } else {
            System.out.println("No hay cambios permitidos para este estado.");
            return;
        }

        int opcion = -1;

        while (true) {
            System.out.print("Seleccione opción: ");
            if (!sc.hasNextInt()) {
                System.out.println("Error: debe ingresar un número válido.");
                sc.nextLine();
                continue;
            }

            opcion = sc.nextInt();
            sc.nextLine();

            if (actual == Estado.RESUELTA && opcion != 1) {
                System.out.println("Opción no válida. Solo puede cerrar la incidencia.");
                continue;
            }

            if (opcion < 1 || opcion > 3) {
                System.out.println("Opción no válida. Intente de nuevo.");
                continue;
            }

            break;
        }

        switch (actual) {

            case EN_CURSO:
                if (opcion == 1) {
                    inc.registrarCambioEstado(Estado.EN_ESPERA, t.getNombre());
                    System.out.println("Estado cambiado a EN_ESPERA");
                } else if (opcion == 2) {
                    inc.registrarCambioEstado(Estado.RESUELTA, t.getNombre());
                    System.out.println("Estado cambiado a RESUELTA");
                } else {
                    System.out.print("Descripción de la solución: ");
                    String solucion = sc.nextLine();
                    inc.registrarCambioEstado(Estado.CERRADA, t.getNombre());
                    inc.cerrarIncidencia(solucion);
                    System.out.println("Incidencia cerrada correctamente");
                }
                break;

            case EN_ESPERA:
                if (opcion == 1) {
                    inc.registrarCambioEstado(Estado.EN_CURSO, t.getNombre());
                    System.out.println("Estado cambiado a EN_CURSO");
                } else if (opcion == 2) {
                    inc.registrarCambioEstado(Estado.RESUELTA, t.getNombre());
                    System.out.println("Estado cambiado a RESUELTA");
                } else {
                    System.out.print("Descripción de la solución: ");
                    String solucion = sc.nextLine();
                    inc.registrarCambioEstado(Estado.CERRADA, t.getNombre());
                    inc.cerrarIncidencia(solucion);
                    System.out.println("Incidencia cerrada correctamente");
                }
                break;

            case RESUELTA:
                System.out.print("Descripción de la solución final: ");
                String solucion = sc.nextLine();
                inc.registrarCambioEstado(Estado.CERRADA, t.getNombre());
                inc.cerrarIncidencia(solucion);
                System.out.println("Incidencia cerrada correctamente");
                break;

            default:
                System.out.println("No se puede cambiar el estado.");
        }
    }

    // Filtro de Incidencias
    public void filtrarIncidencias(Scanner sc, TecnicoController tecnicoController) {

        while (true) {
            System.out.println("\n--- Filtro de Incidencias ---");
            System.out.println("1. Por Categoría");
            System.out.println("2. Por Prioridad");
            System.out.println("3. Por Estado");
            System.out.println("4. Por Técnico Asignado");
            System.out.println("5. Por Rango de Fechas");
            System.out.println("0. Volver");
            System.out.print("Opción: ");

            int opcion = Utils.leerOpcion(sc);

            switch (opcion) {
                case 1 -> filtrarPorCategoria(sc);
                case 2 -> filtrarPorPrioridad(sc);
                case 3 -> filtrarPorEstado(sc);
                case 4 -> filtrarPorTecnico(sc, tecnicoController);
                case 5 -> filtrarPorFechas(sc);
                case 0 -> { return; }
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void filtrarPorCategoria(Scanner sc) {
        System.out.println("\nCategorías disponibles:");
        int i = 1;
        for (Categoria c : Categoria.values()) {
            System.out.println(i + ". " + c);
            i++;
        }

        System.out.print("Seleccione categoría: ");
        int opcion = Utils.leerOpcion(sc);

        if (opcion < 1 || opcion > Categoria.values().length) {
            System.out.println("Opción inválida");
            return;
        }

        Categoria categoria = Categoria.values()[opcion - 1];

        List<Incidencia> lista = repo.filtrar(
                categoria, null, null, null, null, null
        );

        mostrarResultados(lista);
    }

    private void filtrarPorPrioridad(Scanner sc) {
        System.out.println("\nPrioridades disponibles:");
        int i = 1;
        for (Prioridad p : Prioridad.values()) {
            System.out.println(i + ". " + p);
            i++;
        }

        System.out.print("Seleccione prioridad: ");
        int opcion = Utils.leerOpcion(sc);

        if (opcion < 1 || opcion > Prioridad.values().length) {
            System.out.println("Opción inválida");
            return;
        }

        Prioridad prioridad = Prioridad.values()[opcion - 1];

        List<Incidencia> lista = repo.filtrar(
                null, prioridad, null, null, null, null
        );

        mostrarResultados(lista);
    }

    private void filtrarPorEstado(Scanner sc) {
        System.out.println("\nEstados disponibles:");
        int i = 1;
        for (Estado e : Estado.values()) {
            System.out.println(i + ". " + e);
            i++;
        }

        System.out.print("Seleccione estado: ");
        int opcion = Utils.leerOpcion(sc);

        if (opcion < 1 || opcion > Estado.values().length) {
            System.out.println("Opción inválida");
            return;
        }

        Estado estado = Estado.values()[opcion - 1];

        List<Incidencia> lista = repo.filtrar(
                null, null, estado, null, null, null
        );

        mostrarResultados(lista);
    }

    private void filtrarPorTecnico(Scanner sc, TecnicoController tecnicoController) {
        System.out.print("ID del técnico: ");
        int idTec = Utils.leerIdPositivo(sc, "");

        List<Incidencia> lista = repo.filtrar(
                null, null, null, idTec, null, null
        );

        mostrarResultados(lista);
    }

    private void filtrarPorFechas(Scanner sc) {
        LocalDate inicio = Utils.leerFechaOpcional(sc, "Fecha inicio (dd/MM/yyyy): ");
        LocalDate fin = Utils.leerFechaOpcional(sc, "Fecha fin (dd/MM/yyyy): ");

        List<Incidencia> lista = repo.filtrar(
                null, null, null, null, inicio, fin
        );

        mostrarResultados(lista);
    }

    private void mostrarResultados(List<Incidencia> lista) {
        if (lista.isEmpty()) {
            System.out.println("No se encontraron incidencias");
            return;
        }

        System.out.println("\n--- Resultados ---");
        lista.forEach(System.out::println);
    }




    // Devuelve una lista con las incidencias críticas abiertas más de 4 horas
    public List<Incidencia> obtenerIncidenciasCriticas() {
        List<Incidencia> criticas = new ArrayList<>();

        for (Incidencia inc : repo.obtenerTodas()) {

            if (inc.getEstado() == Estado.ABIERTA &&
                inc.getPrioridad() == Prioridad.CRITICA) {

                long horas = ChronoUnit.HOURS.between(
                        inc.getFechaApertura(),
                        LocalDateTime.now()
                );

                if (horas >= 4) {
                    criticas.add(inc);
                }
            }
        }

        return criticas;
    }

    // Obtener incidencias del mes actual
    public List<Incidencia> obtenerIncidenciasDelMes() {
        YearMonth mesActual = YearMonth.now();

        return repo.obtenerTodas().stream()
                .filter(i -> YearMonth.from(i.getFechaApertura().toLocalDate()).equals(mesActual))
                .toList();
    }

}
