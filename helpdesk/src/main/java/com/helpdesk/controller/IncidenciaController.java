package com.helpdesk.controller;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Tecnico;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Estado;
import com.helpdesk.model.enums.Prioridad;
import com.helpdesk.persistence.IncidenciaRepository;

public class IncidenciaController {

    private IncidenciaRepository repo;

    public IncidenciaController(IncidenciaRepository repo) {
        this.repo = repo;
    }

    //Crear Incidencia
    public void crearIncidencia(String titulo, String descripcion, Categoria categoria,
                                Prioridad prioridad, String solicitante, String email) {

        int id = repo.generarId();
        Incidencia nueva = new Incidencia(id, titulo, descripcion, categoria, prioridad, solicitante, email);
        repo.agregar(nueva);

        System.out.println("Incidencia creada con ID: " + id);
    }

    //Consultar Incidencia
    public void consultarIncidencia(int id) {
        Incidencia i = repo.buscarPorId(id);
        if (i == null) {
            System.out.println("No existe una incidencia con ese ID");
        } else {
            System.out.println(i);
        }
    }

    //Listar Incidencias
    public void listarIncidencias() {
        for (Incidencia i : repo.obtenerTodas()) {
            System.out.println(i);
        }
    }

    //Editar Incidencia
    public void editarIncidencia(int id, String nuevoTitulo, String nuevaDescripcion) {
        Incidencia i = repo.buscarPorId(id);
        if (i == null) {
            System.out.println("Incidencia no encontrada");
            return;
        }

        if (i.getEstado() == Estado.CERRADA) {
            System.out.println("No se puede editar una incidencia cerrada");
            return;
        }

        i.setTitulo(nuevoTitulo);
        i.setDescripcion(nuevaDescripcion);

        System.out.println("Incidencia actualizada");
    }

    //Eliminar Incidencia
    public void eliminarIncidencia(int id) {
        boolean eliminada = repo.eliminar(id);
        if (eliminada) {
            System.out.println("Incidencia eliminada");
        } else {
            System.out.println("No existe una incidencia con ese ID");
        }
    }

    //asignar Técnico
    public void asignarTecnico(int idIncidencia, int idTecnico, TecnicoController tecnicoController) {

    Tecnico t = tecnicoController.buscar(idTecnico);

    if (t == null) {
        System.out.println("Técnico no encontrado");
        return;
    }

    if (!t.isActivo()) {
        System.out.println("No se puede asignar una incidencia a un técnico inactivo");
        return;
    }

    Incidencia inc = repo.buscarPorId(idIncidencia);

    if (inc == null) {
        System.out.println("Incidencia no encontrada");
        return;
    }

    // Asignar técnico
    inc.setTecnicoAsignado(t);

    // Cambiar estado automáticamente
    inc.setEstado(Estado.EN_CURSO);

    System.out.println("Incidencia " + idIncidencia + " asignada a " 
                       + t.getNombre() + " " + t.getApellidos() 
                       + " y ahora está EN_CURSO");
}

}
