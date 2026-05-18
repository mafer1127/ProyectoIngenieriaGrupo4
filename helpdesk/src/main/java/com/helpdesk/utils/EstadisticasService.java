package com.helpdesk.utils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.persistence.IncidenciaRepository;

public class EstadisticasService {

    private IncidenciaRepository repo;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public EstadisticasService(IncidenciaRepository repo) {
        this.repo = repo;
    }

    // ============================================================
    // ESTADÍSTICAS POR ATRIBUTOS
    // ============================================================

    // ------------------------------------------------------------
    // Incidencias por categoría
    // ------------------------------------------------------------
    public void incidenciasPorCategoria() {
        System.out.println("\n--- Incidencias por categoría ---");

        List<Incidencia> lista = repo.obtenerTodas();
        if (lista.isEmpty()) {
            System.out.println("No hay incidencias registradas");
            return;
        }

        lista.stream()
            .collect(Collectors.groupingBy(Incidencia::getCategoria, Collectors.counting()))
            .forEach((cat, count) -> System.out.println(cat + ": " + count));
    }

    // ------------------------------------------------------------
    // Incidencias por prioridad
    // ------------------------------------------------------------
    public void incidenciasPorPrioridad() {
        System.out.println("\n--- Incidencias por prioridad ---");

        List<Incidencia> lista = repo.obtenerTodas();
        if (lista.isEmpty()) {
            System.out.println("No hay incidencias registradas");
            return;
        }

        lista.stream()
            .collect(Collectors.groupingBy(Incidencia::getPrioridad, Collectors.counting()))
            .forEach((p, count) -> System.out.println(p + ": " + count));
    }

    // ------------------------------------------------------------
    // Incidencias por estado
    // ------------------------------------------------------------
    public void incidenciasPorEstado() {
        System.out.println("\n--- Incidencias por estado ---");

        List<Incidencia> lista = repo.obtenerTodas();
        if (lista.isEmpty()) {
            System.out.println("No hay incidencias registradas");
            return;
        }

        lista.stream()
            .collect(Collectors.groupingBy(Incidencia::getEstado, Collectors.counting()))
            .forEach((e, count) -> System.out.println(e + ": " + count));
    }

    // ============================================================
    // TIEMPOS Y SLA
    // ============================================================

    // ------------------------------------------------------------
    // Tiempo promedio de resolución por categoría
    // ------------------------------------------------------------
    public void tiempoPromedioPorCategoria() {
        System.out.println("\n--- Tiempo promedio de resolución por categoría ---");

        List<Incidencia> cerradas = repo.obtenerTodas().stream()
            .filter(i -> i.getFechaCierre() != null)
            .toList();

        if (cerradas.isEmpty()) {
            System.out.println("No hay incidencias resueltas para calcular tiempos");
            return;
        }

        cerradas.stream()
            .collect(Collectors.groupingBy(
                Incidencia::getCategoria,
                Collectors.averagingLong(i ->
                    Duration.between(i.getFechaApertura(), i.getFechaCierre()).toMinutes()
                )
            ))
            .forEach((cat, min) -> System.out.println(cat + ": " + min + " min"));
    }

    // ------------------------------------------------------------
    // SLA para incidencias críticas (< 4h)
    // ------------------------------------------------------------
    public void slaCriticas() {
        List<Incidencia> criticas = repo.obtenerTodas().stream()
            .filter(i -> i.getPrioridad() == Prioridad.CRITICA)
            .filter(i -> i.getFechaCierre() != null)
            .toList();

        if (criticas.isEmpty()) {
            System.out.println("No hay incidencias críticas resueltas para calcular el SLA");
            return;
        }

        long dentroSLA = criticas.stream()
            .filter(i -> Duration.between(
                    i.getFechaApertura(), i.getFechaCierre()
            ).toHours() < 4)
            .count();

        double porcentaje = dentroSLA * 100.0 / criticas.size();

        System.out.println("\n--- SLA: Incidencias Críticas ---");
        System.out.println("Total críticas resueltas : " + criticas.size());
        System.out.println("Resueltas < 4h           : " + dentroSLA);
        System.out.println("Incumplen SLA            : " + (criticas.size() - dentroSLA));
        System.out.printf("Cumplimiento SLA         : %.1f%%%n", porcentaje);
    }

    // ------------------------------------------------------------
    // Tiempo medio de resolución por técnico
    // ------------------------------------------------------------
    public void tiempoMedioPorTecnico() {
        List<Incidencia> cerradas = repo.obtenerTodas().stream()
            .filter(i -> i.getFechaCierre() != null && i.getTecnicoAsignado() != null)
            .toList();

        if (cerradas.isEmpty()) {
            System.out.println("\nNo hay incidencias resueltas con técnico asignado.");
            return;
        }

        System.out.println("\n--- Tiempo medio de resolución por técnico ---");

        cerradas.stream()
            .collect(Collectors.groupingBy(
                Incidencia::getTecnicoAsignado,
                Collectors.averagingLong(i ->
                    Duration.between(i.getFechaApertura(), i.getFechaCierre()).toMinutes()
                )
            ))
            .forEach((tec, min) ->
                System.out.println(tec.getNombre() + " " + tec.getApellidos() + ": " + min + " min")
            );
    }
}
