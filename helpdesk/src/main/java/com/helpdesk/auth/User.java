package com.helpdesk.auth;

public class User {
    private String username;
    private String password;
    private Rol rol;

    public User(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Rol getRol() { return rol; }
}
