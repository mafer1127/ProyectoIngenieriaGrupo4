package com.helpdesk.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.helpdesk.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonPersistence {

    private static final String DATA_DIR         = System.getProperty("user.home") + "/.helpdesk/";
    private static final String INCIDENCIAS_FILE = DATA_DIR + "incidencias.json";
    private static final String TECNICOS_FILE    = DATA_DIR + "tecnicos.json";
    private static final String USUARIOS_FILE    = DATA_DIR + "usuarios.json";
    private static final String HISTORIAL_FILE   = DATA_DIR + "historial.json";
    private static final DateTimeFormatter DTF   = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Gson gson;

    public JsonPersistence() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, t, ctx) ->
                        src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(DTF)))
                .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, t, ctx) ->
                        json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString(), DTF))
                .create();
        ensureDir();
    }

    private void ensureDir() {
        try { Files.createDirectories(Paths.get(DATA_DIR)); }
        catch (IOException e) { throw new RuntimeException("No se puede crear directorio de datos", e); }
    }

    public List<Incidencia>      cargarIncidencias() { return cargar(INCIDENCIAS_FILE, new TypeToken<List<Incidencia>>(){}.getType()); }
    public void guardarIncidencias(List<Incidencia> l) { guardar(INCIDENCIAS_FILE, l); }

    public List<Tecnico>         cargarTecnicos()    { return cargar(TECNICOS_FILE, new TypeToken<List<Tecnico>>(){}.getType()); }
    public void guardarTecnicos(List<Tecnico> l)     { guardar(TECNICOS_FILE, l); }

    public List<Usuario>         cargarUsuarios()    { return cargar(USUARIOS_FILE, new TypeToken<List<Usuario>>(){}.getType()); }
    public void guardarUsuarios(List<Usuario> l)     { guardar(USUARIOS_FILE, l); }

    public List<HistorialCambio> cargarHistorial()   { return cargar(HISTORIAL_FILE, new TypeToken<List<HistorialCambio>>(){}.getType()); }
    public void guardarHistorial(List<HistorialCambio> l) { guardar(HISTORIAL_FILE, l); }

    @SuppressWarnings("unchecked")
    private <T> List<T> cargar(String path, Type type) {
        File f = new File(path);
        if (!f.exists()) return new ArrayList<>();
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            List<T> result = gson.fromJson(r, type);
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            throw new RuntimeException("Archivo de datos corrupto: " + path, e);
        }
    }

    private void guardar(String path, Object data) {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
            gson.toJson(data, w);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar datos en: " + path, e);
        }
    }
}
