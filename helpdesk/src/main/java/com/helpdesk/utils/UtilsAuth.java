package com.helpdesk.utils;

import java.util.Scanner;

import com.helpdesk.auth.AuthService;
import com.helpdesk.auth.User;

public class UtilsAuth {

    // VALIDAR NOMBRE DE USUARIO
    public static String leerUsernameValido(Scanner sc) {
        String username;

        while (true) {
            System.out.print("Nuevo usuario: ");
            username = sc.nextLine().trim();

            if (!username.matches("^[A-Za-z]+$")) {
                System.out.println("Error: el usuario solo puede contener letras (A-Z, a-z).");
                continue;
            }

            return username;
        }
    }

    // VALIDAR CONTRASEÑA SEGURA
    public static String leerPasswordSegura(Scanner sc) {
        String password;

        while (true) {
            System.out.print("Contraseña: ");
            password = sc.nextLine().trim();

            if (!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[.,?!¿¡])[A-Za-z0-9.,?!¿¡]{8,}$")) {
                System.out.println("Contraseña inválida. Debe tener:");
                System.out.println("- Mínimo 8 caracteres");
                System.out.println("- Al menos 1 mayúscula");
                System.out.println("- Al menos 1 número");
                System.out.println("- Al menos 1 símbolo permitido: . , ? ! ¿ ¡");
                continue;
            }

            return password;
        }
    }

    // VALIDAR OPCIÓN 1 O 2 
    public static int leerOpcionRol(Scanner sc) {
        int tipo;

        while (true) {
            System.out.println("Tipo de usuario:");
            System.out.println("1. Usuario");
            System.out.println("2. Técnico");
            System.out.print("Opción: ");

            if (!sc.hasNextInt()) {
                System.out.println("Opción no válida. Debes ingresar 1 o 2");
                sc.nextLine();
                continue;
            }

            tipo = sc.nextInt();
            sc.nextLine();

            if (tipo == 1 || tipo == 2) {
                return tipo;
            }

            System.out.println("Opción no válida. Debes ingresar 1 o 2");
        }
    }


    // LOGIN COMPLETO (3 intentos y contraseña)
    public static User loginSeguro(Scanner sc, AuthService auth) {

        System.out.println("\n=== LOGIN ===");

        int intentosUsuario = 3;
        User user = null;

        // VALIDAR USUARIO
        while (intentosUsuario > 0) {

            System.out.print("Usuario: ");
            String username = sc.nextLine().trim();

            user = auth.findUser(username);

            if (user != null) {
                break;
            }

            intentosUsuario--;
            System.out.println("Usuario incorrecto. Intentos restantes: " + intentosUsuario);

            if (intentosUsuario == 0) {
                System.out.println("Demasiados intentos fallidos. Saliendo del sistema...");
                return null;
            }
        }

        // VALIDAR CONTRASEÑA
        int intentosPass = 3;

        while (intentosPass > 0) {

            System.out.print("Contraseña: ");
            String password = sc.nextLine().trim();

            if (user.getPassword().equals(password)) {
                return user; // LOGIN EXITOSO
            }

            intentosPass--;
            System.out.println("Contraseña incorrecta. Intentos restantes: " + intentosPass);

            if (intentosPass == 0) {
                System.out.println("Demasiados intentos fallidos. Saliendo del sistema...");
                return null;
            }
        }

        return null;
    }
}
