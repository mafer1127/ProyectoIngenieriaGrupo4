package com.helpdesk.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.helpdesk.model.Usuario;
import com.helpdesk.persistence.JsonPersistence;

public class UsuarioService {

    private final JsonPersistence persistence;
    private List<Usuario> usuarios;

    public UsuarioService(JsonPersistence persistence) {
        this.persistence = persistence;
        this.usuarios    = persistence.cargarUsuarios();
        if (usuarios.isEmpty()) crearUsuariosPorDefecto();
    }

    private void crearUsuariosPorDefecto() {
        usuarios.add(new Usuario("tecnico1", hash("tecnico123"),"Carlos García",    "carlos@empresa.com",   Usuario.Rol.TECNICO));
        usuarios.add(new Usuario("usuario1", hash("usuario123"),"Ana Martínez",     "ana@empresa.com",      Usuario.Rol.USUARIO_FINAL));
        persistence.guardarUsuarios(usuarios);
    }

    public Optional<Usuario> login(String username, String password) {
    String h = hash(password);

    // === DEBUG: ver qué usuarios hay cargados ===
    System.out.println("=== DEBUG USUARIOS CARGADOS ===");
    for (Usuario u : usuarios) {
        System.out.println(
            "ID=" + u.getId() +
            " | EMAIL=" + u.getEmail() +
            " | HASH=" + u.getPasswordHash() +
            " | ACTIVO=" + u.isActivo()
        );
    }
    System.out.println("================================");
    // ===============================================

    for (Usuario u : usuarios) {
        if (!u.isActivo()) continue;

        // Coincide por username (id)
        if (u.getId() != null && u.getId().equals(username) &&
            u.getPasswordHash() != null && u.getPasswordHash().equals(h)) {
            return Optional.of(u);
        }

        // Coincide por email
        if (u.getEmail() != null && u.getEmail().equals(username) &&
            u.getPasswordHash() != null && u.getPasswordHash().equals(h)) {
            return Optional.of(u);
        }
    }

    return Optional.empty();
}


    public List<Usuario> listarUsuarios() { return Collections.unmodifiableList(usuarios); }

    public void crearUsuario(Usuario u) {
        if (usuarios.stream().anyMatch(x -> x.getId().equals(u.getId())))
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        usuarios.add(u);
        persistence.guardarUsuarios(usuarios);
    }

    public static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
