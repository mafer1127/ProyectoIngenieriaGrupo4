package com.helpdesk.utils;

import java.util.Scanner;

public class Utils {

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
}
