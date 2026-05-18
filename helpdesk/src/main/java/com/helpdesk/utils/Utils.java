package com.helpdesk.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Utils {

    public static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //Leer opciones de menú, validando que sean enteros
    public static int leerOpcion(Scanner sc) {
        while (!sc.hasNextInt()) {
            System.out.println("Opción no válida. Ingresa un número válido");
            sc.nextLine();
            System.out.print("Opción: ");
        }
        int op = sc.nextInt();
        sc.nextLine();
        return op;
    }

    //validación de datos para ID entero positivo
    public static int leerIdPositivo(Scanner sc, String mensaje) {
        int id;

        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();

            if (!input.matches("\\d+")) {
                System.out.println("Error: el ID debe contener solo números enteros positivos");
                continue;
            }

            id = Integer.parseInt(input);

            if (id <= 0) {
                System.out.println("Error: el ID debe ser mayor que cero");
                continue;
            }

            return id;
        }
    }

    //validación de formato de email
    public static String leerEmailValido(Scanner sc, String mensaje) {
        String email;

        while (true) {
            System.out.print(mensaje);
            email = sc.nextLine().trim();

            // Expresión regular para validar email
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("Error: formato de email inválido. Ejemplo válido: usuario@correo.com");
                continue;
            }

            return email;
        }
    }

    //validación de nombres (solo letras, espacios simples, tildes y ñ)
    public static String leerNombreValido(Scanner sc, String mensaje) {
        String nombre;

        while (true) {
            System.out.print(mensaje);
            nombre = sc.nextLine().trim();

            // Solo letras, espacios simples, tildes y ñ
            if (!nombre.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ]+( [A-Za-zÁÉÍÓÚáéíóúÑñ]+)*$")) {
                System.out.println("Error: el nombre solo puede contener letras y espacios simples");
                continue;
            }

            return nombre;
        }
    }

    //validación de opciones de enum
    public static <T extends Enum<T>> T leerEnumValido(Scanner sc, String mensaje, Class<T> enumClass) {
        T valor = null;

        while (true) {
            System.out.print(mensaje);

            String input = sc.nextLine().trim().toUpperCase();

            try {
                valor = Enum.valueOf(enumClass, input);
                return valor;

            } catch (IllegalArgumentException e) {
                System.out.println("Valor inválido. Las opciones válidas son:");

                for (T v : enumClass.getEnumConstants()) {
                    System.out.println(" - " + v.name());
                }

                System.out.println("Inténtelo de nuevo\n");
            }
        }
    }

    // Lectura de opciones de enum, pero permite dejar en blanco para omitir el filtro
    public static <T extends Enum<T>> T leerEnumOpcional(Scanner sc, String mensaje, Class<T> tipo) {
    while (true) {
        System.out.print(mensaje);
        String input = sc.nextLine().trim().toUpperCase();

        // ENTER  no aplicar filtro
        if (input.isBlank()) {
            return null;
        }

        try {
            return Enum.valueOf(tipo, input);
        } catch (IllegalArgumentException e) {
            System.out.println("Valor inválido. Opciones válidas:");
            for (T val : tipo.getEnumConstants()) {
                System.out.print(val + " ");
            }
            System.out.println("\nIntente de nuevo\n");
        }
    }
}

    // Lectura de fechas, permite dejar en blanco para omitir el filtro
    public static LocalDate leerFechaOpcional(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();

            if (input.isBlank()) return null;

            try {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use dd/MM/yyyy o ENTER para omitir");
            }
        }
    }


}
