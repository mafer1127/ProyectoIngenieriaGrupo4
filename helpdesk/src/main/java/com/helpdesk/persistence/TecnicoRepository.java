package com.helpdesk.persistence;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.helpdesk.model.Tecnico;

public class TecnicoRepository {

    private ArrayList<Tecnico> tecnicos = new ArrayList<>();
    private int contadorId = 1;

    // ============================================================
    // CONSTRUCTORES
    // ============================================================

    // Constructor con carga inicial
    public TecnicoRepository(List<Tecnico> listaInicial) {
        if (listaInicial != null) {
            this.tecnicos = new ArrayList<>(listaInicial);

            for (Tecnico t : listaInicial) {
                if (t.getId() >= contadorId) {
                    contadorId = t.getId() + 1;
                }
            }
        }
    }

    // Constructor vacío
    public TecnicoRepository() {
    }

    // ============================================================
    // RECARGA DE DATOS
    // ============================================================

    public void setTecnicos(List<Tecnico> nuevos) {
        this.tecnicos = new ArrayList<>(nuevos);

        for (Tecnico t : nuevos) {
            if (t.getId() >= contadorId) {
                contadorId = t.getId() + 1;
            }
        }
    }

    // ============================================================
    // CRUD BÁSICO
    // ============================================================

    // ID autoincremental
    public int generarId() {
        return contadorId++;
    }

    public void agregar(Tecnico t) {
        tecnicos.add(t);
    }

    public Tecnico buscarPorId(int id) {
        return tecnicos.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Tecnico> getAll() {
        return tecnicos;
    }

    // ============================================================
    // ====================  PERSISTENCIA JSON  ====================
    // ============================================================

    // ------------------------------------------------------------
    // Guardar texto en archivo
    // ------------------------------------------------------------
    private void guardarTexto(String ruta, String contenido) {
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(contenido);
            System.out.println("Técnicos guardados en: " + ruta);
        } catch (IOException e) {
            System.out.println("Error al guardar JSON: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // Leer texto de archivo
    // ------------------------------------------------------------
    private String leerTexto(String ruta) {
        try {
            return Files.readString(Path.of(ruta));
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + ruta);
            return "";
        }
    }

    // ------------------------------------------------------------
    // Guardar técnicos en JSON
    // ------------------------------------------------------------
    public void guardarEnJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < tecnicos.size(); i++) {
            Tecnico t = tecnicos.get(i);

            sb.append("  {\n");
            sb.append("    \"id\": ").append(t.getId()).append(",\n");
            sb.append("    \"nombre\": \"").append(t.getNombre()).append("\",\n");
            sb.append("    \"apellidos\": \"").append(t.getApellidos()).append("\",\n");
            sb.append("    \"email\": \"").append(t.getEmail()).append("\",\n");
            sb.append("    \"especialidad\": \"").append(t.getEspecialidad()).append("\",\n");
            sb.append("    \"activo\": ").append(t.isActivo()).append("\n");
            sb.append("  }");

            if (i < tecnicos.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("]");

        guardarTexto("tecnicos.json", sb.toString());
    }

    // ------------------------------------------------------------
    // Cargar técnicos desde JSON externo
    // ------------------------------------------------------------
    public boolean cargarDesdeArchivoExterno(String ruta) {
        String contenido = leerTexto(ruta);

        if (contenido.isEmpty()) return false;

        contenido = contenido.replace("[", "")
                             .replace("]", "")
                             .trim();

        if (contenido.isEmpty()) return false;

        String[] objetos = contenido.split("\\},\\s*\\{");

        tecnicos.clear();

        for (String obj : objetos) {
            obj = obj.replace("{", "").replace("}", "").trim();

            String[] campos = obj.split(",");

            int id = 0;
            String nombre = "";
            String apellidos = "";
            String email = "";
            String especialidad = "";
            boolean activo = false;

            for (String campo : campos) {
                String[] partes = campo.split(":");
                String key = partes[0].replace("\"", "").trim();
                String value = partes[1].replace("\"", "").trim();

                switch (key) {
                    case "id": id = Integer.parseInt(value); break;
                    case "nombre": nombre = value; break;
                    case "apellidos": apellidos = value; break;
                    case "email": email = value; break;
                    case "especialidad": especialidad = value; break;
                    case "activo": activo = Boolean.parseBoolean(value); break;
                }
            }

            tecnicos.add(new Tecnico(id, nombre, apellidos, email, especialidad, activo));
        }

        // Ajustar contadorId
        contadorId = 1;
        for (Tecnico t : tecnicos) {
            if (t.getId() >= contadorId) contadorId = t.getId() + 1;
        }

        System.out.println("Técnicos cargados desde JSON");
        return true;
    }
}
