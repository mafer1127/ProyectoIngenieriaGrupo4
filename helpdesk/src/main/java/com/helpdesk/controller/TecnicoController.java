package com.helpdesk.controller;

import com.helpdesk.exceptions.ValidacionDatosException;
import com.helpdesk.model.Tecnico;
import com.helpdesk.model.enums.Especialidad;
import com.helpdesk.persistence.TecnicoRepository;

public class TecnicoController {

    private TecnicoRepository repo;

    public TecnicoController(TecnicoRepository repo) {
        this.repo = repo;
    }

    public void crearTecnico(String nombre, String apellidos, String email, Especialidad esp) {
        Tecnico t = new Tecnico(nombre, apellidos, email, esp);
        repo.agregar(t);
        System.out.println("Técnico creado con ID: " + t.getId());
    }

    public Tecnico buscar(int id) {
        Tecnico t = repo.buscarPorId(id);
        if (t == null) {
            throw new ValidacionDatosException("El técnico no existe");
        }
        return t;
    }

    public void activar(int id) {
        Tecnico t = buscar(id);
        t.activar();
        System.out.println("Técnico activado");
    }

    public void desactivar(int id) {
        Tecnico t = buscar(id);
        t.desactivar();
        System.out.println("Técnico desactivado");
    }

    public void listarTecnicos() {
        repo.getAll().forEach(System.out::println);
    }
}