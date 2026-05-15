package com.helpdesk.controller;

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
        //por si esta vacía
        if (repo.obtenerTodas().isEmpty()) {
        System.out.println("No hay incidencias registradas");
        return;
    }

        for (Incidencia i : repo.obtenerTodas()) {
            System.out.println(i);
        }
    }

    //Editar Incidencia
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
    public void asignarTecnico(int idIncidencia, Scanner sc, TecnicoController tecnicoController) 
        throws ValidacionDatosException, TecnicoNoDisponibleException   {
    
    //validar existencia de incidencia 
    Incidencia inc = repo.buscarPorId(idIncidencia);
    if (inc == null) {
        throw new ValidacionDatosException("La incidencia no existe");
    }

    //validar existencia de técnico
    int idTecnico = Utils.leerIdPositivo(sc, "ID del técnico: ");
    Tecnico t = tecnicoController.buscar(idTecnico);
    if (t == null) {
        throw new ValidacionDatosException("El técnico no existe");
    }

    if (!t.isActivo()) {
        throw new TecnicoNoDisponibleException("No se puede asignar una incidencia a un técnico inactivo");
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
