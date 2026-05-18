package com.helpdesk.persistence;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonManager {

    // -------------------------
    // GUARDAR TEXTO EN ARCHIVO
    // -------------------------
    public static void guardarTexto(String ruta, String contenido) {
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(contenido);
            System.out.println("Guardado en: " + ruta);
        } catch (IOException e) {
            System.out.println("Error al guardar JSON: " + e.getMessage());
        }
    }

    // -------------------------
    // LEER TEXTO DE ARCHIVO
    // -------------------------
    public static String leerTexto(String ruta) {
        try {
            return Files.readString(Path.of(ruta));
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + ruta);
            return "";
        }
    }
}
