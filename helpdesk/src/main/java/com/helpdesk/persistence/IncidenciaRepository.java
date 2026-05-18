package com.helpdesk.persistence;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.helpdesk.model.Incidencia;
import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Estado;
import com.helpdesk.model.enums.Prioridad;

public class IncidenciaRepository {

    private ArrayList<Incidencia> incidencias = new ArrayList<>();
    private int contadorId = 1;


    // ============================================================
    // CONSTRUCTOR CON LISTA INICIAL
    // ============================================================
    public IncidenciaRepository(List<Incidencia> listaInicial) {
        if (listaInicial != null) {
            this.incidencias = new ArrayList<>(listaInicial);

            for (Incidencia i : listaInicial) {
                if (i.getId() >= contadorId) {
                    contadorId = i.getId() + 1;
                }
            }
        }
    }

    // ============================================================
    // CONSTRUCTOR VACÍO
    // ============================================================
    public IncidenciaRepository() {}

    // ============================================================
    // SETTER PARA RECARGAR DATOS
    // ============================================================
    public void setIncidencias(List<Incidencia> nuevas) {
        this.incidencias = new ArrayList<>(nuevas);

        for (Incidencia i : nuevas) {
            if (i.getId() >= contadorId) {
                contadorId = i.getId() + 1;
            }
        }
    }

    // ============================================================
    // CRUD BÁSICO
    // ============================================================
    public int generarId() {
        return contadorId++;
    }

    public void agregar(Incidencia i) {
        incidencias.add(i);
    }

    public Incidencia buscarPorId(int id) {
        for (Incidencia i : incidencias) {
            if (i.getId() == id) return i;
        }
        return null;
    }

    public ArrayList<Incidencia> obtenerTodas() {
        return incidencias;
    }

    public boolean eliminar(int id) {
        return incidencias.removeIf(i -> i.getId() == id);
    }

    // ============================================================
    // FILTROS
    // ============================================================
    public List<Incidencia> filtrar(
        Categoria categoria,
        Prioridad prioridad,
        Estado estado,
        Integer idTecnico,
        LocalDate fechaInicio,
        LocalDate fechaFin
    ) {
        return incidencias.stream()
            .filter(i -> categoria == null || i.getCategoria() == categoria)
            .filter(i -> prioridad == null || i.getPrioridad() == prioridad)
            .filter(i -> estado == null || i.getEstado() == estado)
            .filter(i -> idTecnico == null ||
                    (i.getTecnicoAsignado() != null && i.getTecnicoAsignado().getId() == idTecnico))
            .filter(i -> {
                if (fechaInicio == null && fechaFin == null) return true;
                LocalDate fecha = i.getFechaApertura().toLocalDate();
                if (fechaInicio != null && fecha.isBefore(fechaInicio)) return false;
                if (fechaFin != null && fecha.isAfter(fechaFin)) return false;
                return true;
            })
            .toList();
    }

    // ============================================================
    // ===============  PERSISTENCIA JSON  =================
    // ============================================================

    // -------------------------
    // Guardar texto en archivo
    // -------------------------
    private void guardarTexto(String ruta, String contenido) {
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(contenido);
            System.out.println("Incidencias guardadas en: " + ruta);
        } catch (IOException e) {
            System.out.println("Error al guardar JSON: " + e.getMessage());
        }
    }

    // -------------------------
    // Leer texto de archivo
    // -------------------------
    private String leerTexto(String ruta) {
        try {
            return Files.readString(Path.of(ruta));
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + ruta);
            return "";
        }
    }

    // -------------------------
    // GUARDAR INCIDENCIAS EN JSON
    // -------------------------
    public void guardarEnJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < incidencias.size(); i++) {
            Incidencia inc = incidencias.get(i);

            sb.append("  {\n");
            sb.append("    \"id\": ").append(inc.getId()).append(",\n");
            sb.append("    \"titulo\": \"").append(inc.getTitulo()).append("\",\n");
            sb.append("    \"descripcion\": \"").append(inc.getDescripcion()).append("\",\n");
            sb.append("    \"categoria\": \"").append(inc.getCategoria()).append("\",\n");
            sb.append("    \"prioridad\": \"").append(inc.getPrioridad()).append("\",\n");
            sb.append("    \"estado\": \"").append(inc.getEstado()).append("\",\n");
            sb.append("    \"fechaApertura\": \"").append(inc.getFechaApertura()).append("\",\n");
            sb.append("    \"fechaCierre\": ")
                .append(inc.getFechaCierre() == null ? "null" : "\"" + inc.getFechaCierre() + "\"")
                .append(",\n");
            sb.append("    \"solicitante\": \"").append(inc.getSolicitante()).append("\",\n");
            sb.append("    \"emailSolicitante\": \"").append(inc.getEmailSolicitante()).append("\",\n");
            sb.append("    \"idTecnico\": ").append(
                inc.getIdTecnico() == null ? "null" : inc.getIdTecnico()
            ).append("\n");
            sb.append("  }");

            if (i < incidencias.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("]");

        guardarTexto("incidencias.json", sb.toString());
    }

    // -------------------------
    // CARGAR INCIDENCIAS DESDE JSON EXTERNO
    // -------------------------
    public boolean cargarDesdeArchivoExterno(String ruta) {
        String contenido = leerTexto(ruta);

        // Si no se pudo leer o está vacío → fallo
        if (contenido.isEmpty()) {
            return false;
        }

        contenido = contenido.replace("[", "")
                            .replace("]", "")
                            .trim();

        if (contenido.isEmpty()) {
            return false;
        }

        String[] objetos = contenido.split("\\},\\s*\\{");

        incidencias.clear();

        for (String obj : objetos) {
            obj = obj.replace("{", "").replace("}", "").trim();

            String[] campos = obj.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

            int id = 0;
            String titulo = "";
            String descripcion = "";
            Categoria categoria = null;
            Prioridad prioridad = null;
            Estado estado = null;
            LocalDateTime fechaApertura = null;
            LocalDateTime fechaCierre = null;
            String solicitante = "";
            String emailSolicitante = "";
            Integer idTecnico = null;

            for (String campo : campos) {
                int index = campo.indexOf(":");
                if (index == -1) continue;

                String key = campo.substring(0, index).replace("\"", "").trim();
                String value = campo.substring(index + 1).trim();

                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }

                switch (key) {
                    case "id": id = Integer.parseInt(value); break;
                    case "titulo": titulo = value; break;
                    case "descripcion": descripcion = value; break;
                    case "categoria": categoria = Categoria.valueOf(value); break;
                    case "prioridad": prioridad = Prioridad.valueOf(value); break;
                    case "estado": estado = Estado.valueOf(value); break;
                    case "fechaApertura":
                        fechaApertura = value.equals("null") ? null : LocalDateTime.parse(value);
                        break;
                    case "fechaCierre":
                        fechaCierre = value.equals("null") ? null : LocalDateTime.parse(value);
                        break;
                    case "solicitante": solicitante = value; break;
                    case "emailSolicitante": emailSolicitante = value; break;
                    case "idTecnico": idTecnico = value.equals("null") ? null : Integer.parseInt(value); break;
                }
            }

            Incidencia nueva = new Incidencia();
            nueva.setHistorial(new ArrayList<>());
            nueva.setId(id);
            nueva.setTitulo(titulo);
            nueva.setDescripcion(descripcion);
            nueva.setCategoria(categoria);
            nueva.setPrioridad(prioridad);
            nueva.setSolicitante(solicitante);
            nueva.setEmailSolicitante(emailSolicitante);
            nueva.setEstado(estado);
            nueva.setFechaApertura(fechaApertura);
            nueva.setFechaCierre(fechaCierre);
            nueva.setIdTecnico(idTecnico);

            incidencias.add(nueva);
        }

        contadorId = 1;
        for (Incidencia i : incidencias) {
            if (i.getId() >= contadorId) contadorId = i.getId() + 1;
        }

        System.out.println("Incidencias cargadas desde JSON");
        return true;
    }
}

