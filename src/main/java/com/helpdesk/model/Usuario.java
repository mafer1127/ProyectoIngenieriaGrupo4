package com.helpdesk.model;

public class Usuario {

    public enum Rol { USUARIO_FINAL, TECNICO }

    private String id;
    private String nombre;
    private String email;
    private Rol rol;
    private String passwordHash;
    private boolean activo = true;
    public Usuario() {}
    public Usuario(String username, String passwordHash, String nombreCompleto, String email, Rol rol) {
        this.id = username;
        this.passwordHash = passwordHash;
        this.nombre = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.activo = true;
}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
     public boolean isActivo() { return activo; }        
    public void setActivo(boolean activo) { this.activo = activo; }


    @Override public String toString()           { return nombre + " (" + rol + ")"; }
}
