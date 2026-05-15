package com.helpdesk.model;
import com.helpdesk.model.enums.Especialidad;

public class Tecnico {

    private static int contador = 1;

    private int id;
    private String nombre;
    private String apellidos;
    private String email;
    private Especialidad especialidad;
    private boolean activo;

    public Tecnico(String nombre, String apellidos, String email, Especialidad especialidad) {
        this.id = contador++;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.especialidad = especialidad;
        this.activo = true; // por defecto activo
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    @Override
    public String toString() {
        return "\nTécnico ID: " + id +
           "\nNombre: " + nombre + " " + apellidos +
           "\nEmail: " + email +
           "\nEspecialidad: " + especialidad +
           "\nActivo: " + activo +
           "\n-----------------------------";
    }
}
