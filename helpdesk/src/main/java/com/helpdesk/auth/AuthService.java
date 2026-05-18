package com.helpdesk.auth;

import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private List<User> users = new ArrayList<>();

    public AuthService() {
        // Usuarios de prueba
        users.add(new User("tecnico", "123", Rol.TECNICO));
        users.add(new User("usuario", "123", Rol.USUARIO));
    }

    public User login(String username, String password) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean exists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }

    public void register(String username, String password, Rol rol) {
        users.add(new User(username, password, rol));
    }

    public User findUser(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }
}
