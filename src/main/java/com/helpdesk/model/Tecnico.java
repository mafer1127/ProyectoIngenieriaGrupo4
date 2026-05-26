package com.helpdesk.model;

import java.util.UUID;

public class Tecnico {

    public enum Especialidad { SISTEMAS, REDES, USUARIO_FINAL, SEGURIDAD }

    private String id;
    private String nombre;
    private String apellidos;
    private String emailCorporativo;
    private Especialidad especialidad;
    private boolean activo;

    public Tecnico() {}

    public Tecnico(String nombre, String apellidos, String emailCorporativo,
                   Especialidad especialidad) {
        this.id               = UUID.randomUUID().toString();
        this.nombre           = nombre;
        this.apellidos        = apellidos;
        this.emailCorporativo = emailCorporativo;
        this.especialidad     = especialidad;
        this.activo           = true;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────
    public String getId()                           { return id; }
    public void   setId(String id)                  { this.id = id; }
    public String getNombre()                       { return nombre; }
    public void   setNombre(String n)               { this.nombre = n; }
    public String getApellidos()                    { return apellidos; }
    public void   setApellidos(String a)            { this.apellidos = a; }
    public String getEmailCorporativo()             { return emailCorporativo; }
    public void   setEmailCorporativo(String e)     { this.emailCorporativo = e; }
    public Especialidad getEspecialidad()           { return especialidad; }
    public void   setEspecialidad(Especialidad e)   { this.especialidad = e; }
    public boolean isActivo()                       { return activo; }
    public void   setActivo(boolean a)              { this.activo = a; }

    @Override public String toString() { return getNombreCompleto(); }
}
