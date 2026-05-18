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

    public Tecnico() {
    }


    // CONSTRUCTOR PARA CREAR TÉCNICOS DESDE EL MENÚ
    public Tecnico(String nombre, String apellidos, String email, Especialidad especialidad) {
        this.id = contador++;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.especialidad = especialidad;
        this.activo = true; // por defecto activo
    }

    // CONSTRUCTOR PARA CARGAR TÉCNICOS DESDE JSON
    public Tecnico(int id, String nombre, String apellidos, String email, String especialidad, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.especialidad = Especialidad.valueOf(especialidad); // convertir String → enum
        this.activo = activo; // respetar JSON

        // Ajustar contador para evitar IDs duplicados
        if (id >= contador) {
            contador = id + 1;
        }
    }

    // GETTERS Y SETTERS
    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
        if (id >= contador) {
            contador = id + 1;
        }
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // MÉTODOS EXTRA
    public void activar() { this.activo = true; }
    public void desactivar() { this.activo = false; }

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
