package com.helpdesk.controller;

import com.helpdesk.model.Tecnico;
import com.helpdesk.persistence.TecnicoRepository;

public class TecnicoController {

    private TecnicoRepository repo;

    public TecnicoController(TecnicoRepository repo) {
        this.repo = repo;
    }

    public void crearTecnico(String nombre, String email) {
        int id = repo.getAll().size() + 1;
        Tecnico t = new Tecnico(id, nombre, email, true);
        repo.agregar(t);
        System.out.println(" Técnico registrado correctamente");
    }

    public void listarTecnicos() {
        System.out.println("\n--- LISTA DE TÉCNICOS ---");
        for (Tecnico t : repo.getAll()) {
            System.out.println(t.getId() + " - " + t.getNombre() + " (" + (t.isActivo() ? "Activo" : "Inactivo") + ")");
        }
    }

    public void activar(int id) {
        Tecnico t = repo.buscarPorId(id);
        if (t != null) {
            t.setActivo(true);
            System.out.println("Técnico activado");
        } else {
            System.out.println("Técnico no encontrado");
        }
    }

    public void desactivar(int id) {
        Tecnico t = repo.buscarPorId(id);
        if (t != null) {
            t.setActivo(false);
            System.out.println("Técnico desactivado");
        } else {
            System.out.println("Técnico no encontrado");
        }
    }

    public Tecnico buscar(int id) {
        return repo.buscarPorId(id);
    }

}
