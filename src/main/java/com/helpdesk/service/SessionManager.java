package com.helpdesk.service;

import com.helpdesk.model.Usuario;

public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioActual;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void login(Usuario u)     { this.usuarioActual = u; }
    public void logout()             { this.usuarioActual = null; }
    public Usuario getUsuario()      { return usuarioActual; }
    public boolean isLoggedIn()      { return usuarioActual != null; }


    public boolean esTecnicoOAdmin() {
    return usuarioActual != null &&
           usuarioActual.getRol() == Usuario.Rol.TECNICO;
}
    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombre() : "Anónimo";
    }
}
