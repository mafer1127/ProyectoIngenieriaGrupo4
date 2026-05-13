package com.helpdesk.controller;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.model.enums.Estado;
import com.helpdesk.persistence.IncidenciaRepository;

public class IncidenciaController {

    private IncidenciaRepository repo;

    public IncidenciaController(IncidenciaRepository repo) {
        this.repo = repo;
    }

    public void crearIncidencia(String titulo, String descripcion, Categoria categoria,
                                Prioridad prioridad, String solicitante, String email) {

        int id = repo.generarId();
        Incidencia nueva = new Incidencia(id, titulo, descripcion, categoria, prioridad, solicitante, email);
        repo.agregar(nueva);

        System.out.println("Incidencia creada con ID: " + id);
    }

    public void consultarIncidencia(int id) {
        Incidencia i = repo.buscarPorId(id);
        if (i == null) {
            System.out.println("No existe una incidencia con ese ID.");
        } else {
            System.out.println(i);
        }
    }

    public void listarIncidencias() {
        for (Incidencia i : repo.obtenerTodas()) {
            System.out.println(i);
        }
    }

    public void editarIncidencia(int id, String nuevoTitulo, String nuevaDescripcion) {
        Incidencia i = repo.buscarPorId(id);
        if (i == null) {
            System.out.println("Incidencia no encontrada.");
            return;
        }

        if (i.getEstado() == Estado.CERRADA) {
            System.out.println("No se puede editar una incidencia cerrada.");
            return;
        }

        i.setTitulo(nuevoTitulo);
        i.setDescripcion(nuevaDescripcion);

        System.out.println("Incidencia actualizada.");
    }

    public void eliminarIncidencia(int id) {
        boolean eliminada = repo.eliminar(id);
        if (eliminada) {
            System.out.println("Incidencia eliminada.");
        } else {
            System.out.println("No existe una incidencia con ese ID.");
        }
    }
}
