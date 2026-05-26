package com.helpdesk.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import com.helpdesk.model.HistorialCambio;
import com.helpdesk.model.Incidencia;
import com.helpdesk.model.Incidencia.Estado;
import com.helpdesk.model.Tecnico;
import com.helpdesk.model.Usuario;
import com.helpdesk.persistence.JsonPersistence;

public class IncidenciaService {

    private final JsonPersistence persistence;
    private List<Incidencia> incidencias;
    private List<Tecnico> tecnicos;
    private List<HistorialCambio> historial;

    private static final Map<Estado, Set<Estado>> TRANSICIONES = new HashMap<>();
    static {
        TRANSICIONES.put(Estado.ABIERTA,    Set.of(Estado.EN_CURSO, Estado.EN_ESPERA));
        TRANSICIONES.put(Estado.EN_CURSO,   Set.of(Estado.EN_ESPERA, Estado.RESUELTA));
        TRANSICIONES.put(Estado.EN_ESPERA,  Set.of(Estado.EN_CURSO, Estado.RESUELTA));
        TRANSICIONES.put(Estado.RESUELTA,   Set.of(Estado.CERRADA));
        TRANSICIONES.put(Estado.CERRADA,    Collections.emptySet());
    }

    public IncidenciaService(JsonPersistence persistence) {
        this.persistence = persistence;
        this.incidencias = persistence.cargarIncidencias();
        this.tecnicos    = persistence.cargarTecnicos();
        this.historial   = persistence.cargarHistorial();
    }

    // ─────────────────────────────────────────────────────────────
    // CRUD INCIDENCIAS
    // ─────────────────────────────────────────────────────────────

    public void crearIncidencia(Incidencia i) {
        validarIncidencia(i);

        if (SessionManager.getInstance().isLoggedIn()) {
            i.setCreadoPorUsuarioId(SessionManager.getInstance().getUsuario().getId());
        }

        incidencias.add(i);
        registrarHistorial(i.getId(), null, i.getEstado(), "Incidencia creada");
        guardarIncidencias();
    }

    public void actualizarIncidencia(Incidencia i) {
        validarIncidencia(i);
        int idx = indexOf(i.getId());
        incidencias.set(idx, i);
        guardarIncidencias();
    }

    public void eliminarIncidencia(String id) {
        for (int i = 0; i < incidencias.size(); i++) {
            if (incidencias.get(i).getId().equals(id)) {
                incidencias.remove(i);
                break;
            }
        }

        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i).getIncidenciaId().equals(id)) {
                historial.remove(i);
                i--;
            }
        }

        persistence.guardarHistorial(historial);
        guardarIncidencias();
    }

    public Optional<Incidencia> buscarIncidenciaPorId(String id) {
        for (Incidencia i : incidencias) {
            if (i.getId().equals(id)) return Optional.of(i);
        }
        return Optional.empty();
    }

    public List<Incidencia> listarIncidencias() {
        String uid = SessionManager.getInstance().isLoggedIn()
                ? SessionManager.getInstance().getUsuario().getId() : null;

        boolean esUsuarioFinal = SessionManager.getInstance().isLoggedIn() &&
                SessionManager.getInstance().getUsuario().getRol() == Usuario.Rol.USUARIO_FINAL;

        if (esUsuarioFinal && uid != null) {
            List<Incidencia> res = new ArrayList<>();
            for (Incidencia i : incidencias) {
                if (uid.equals(i.getCreadoPorUsuarioId())) res.add(i);
            }
            return res;
        }

        return new ArrayList<>(incidencias);
    }

    public List<Incidencia> listarTodasLasIncidencias() {
        return new ArrayList<>(incidencias);
    }

    // ─────────────────────────────────────────────────────────────
    // CAMBIO DE ESTADO
    // ─────────────────────────────────────────────────────────────

    public void cambiarEstado(String id, Estado nuevoEstado, String solucion) {
        Incidencia inc = buscarIncidenciaPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Incidencia no encontrada"));

        if (inc.getTecnicoAsignadoId() == null && nuevoEstado != Estado.ABIERTA) {
            throw new IllegalStateException("No se puede cambiar el estado hasta asignar un técnico.");
        }

        Estado anterior = inc.getEstado();

        if (!TRANSICIONES.get(anterior).contains(nuevoEstado)) {
            throw new IllegalStateException("Transición no permitida");
        }

        inc.setEstado(nuevoEstado);

        if (nuevoEstado == Estado.RESUELTA || nuevoEstado == Estado.CERRADA) {
            inc.setFechaResolucion(LocalDateTime.now());
            inc.setDescripcionSolucion(solucion);
        }

        actualizarIncidencia(inc);
        registrarHistorial(id, anterior, nuevoEstado, solucion);
    }

    // ─────────────────────────────────────────────────────────────
    // HISTORIAL
    // ─────────────────────────────────────────────────────────────

    public List<HistorialCambio> historialDeIncidencia(String incId) {
        List<HistorialCambio> res = new ArrayList<>();
        for (HistorialCambio h : historial) {
            if (incId.equals(h.getIncidenciaId())) res.add(h);
        }

        res.sort(Comparator.comparing(HistorialCambio::getFecha));
        return res;
    }

    private void registrarHistorial(String incId, Estado anterior, Estado nuevo, String nota) {
        String usuario = SessionManager.getInstance().getNombreUsuario();
        historial.add(new HistorialCambio(incId, anterior, nuevo, usuario, nota));
        persistence.guardarHistorial(historial);
    }

    // ─────────────────────────────────────────────────────────────
    // FILTROS
    // ─────────────────────────────────────────────────────────────

    public List<Incidencia> filtrar(Incidencia.Categoria categoria,
                                    Incidencia.Prioridad prioridad,
                                    Estado estado,
                                    String tecnicoId,
                                    LocalDateTime desde,
                                    LocalDateTime hasta) {

        List<Incidencia> res = new ArrayList<>();

        for (Incidencia i : listarIncidencias()) {

            if (categoria != null && i.getCategoria() != categoria) continue;
            if (prioridad != null && i.getPrioridad() != prioridad) continue;
            if (estado != null && i.getEstado() != estado) continue;
            if (tecnicoId != null && !tecnicoId.equals(i.getTecnicoAsignadoId())) continue;

            if (desde != null && i.getFechaApertura().isBefore(desde)) continue;
            if (hasta != null && i.getFechaApertura().isAfter(hasta)) continue;

            res.add(i);
        }

        return res;
    }

    // ─────────────────────────────────────────────────────────────
    // RESUMEN / KPIs (sin stream)
    // ─────────────────────────────────────────────────────────────

    public Map<Incidencia.Prioridad, Integer> incidenciasAbiertasPorPrioridad() {
        Map<Incidencia.Prioridad, Integer> map = new EnumMap<>(Incidencia.Prioridad.class);

        for (Incidencia.Prioridad p : Incidencia.Prioridad.values()) map.put(p, 0);

        for (Incidencia i : incidencias) {
            if (i.getEstado() != Estado.CERRADA && i.getEstado() != Estado.RESUELTA) {
                map.put(i.getPrioridad(), map.get(i.getPrioridad()) + 1);
            }
        }

        return map;
    }

    public Map<Incidencia.Categoria, Double> tiempoMedioResolucionPorCategoria() {
        Map<Incidencia.Categoria, Long> suma = new EnumMap<>(Incidencia.Categoria.class);
        Map<Incidencia.Categoria, Integer> cuenta = new EnumMap<>(Incidencia.Categoria.class);

        for (Incidencia.Categoria c : Incidencia.Categoria.values()) {
            suma.put(c, 0L);
            cuenta.put(c, 0);
        }

        for (Incidencia i : incidencias) {
            if (i.getFechaResolucion() != null) {
                long horas = Duration.between(i.getFechaApertura(), i.getFechaResolucion()).toHours();
                suma.put(i.getCategoria(), suma.get(i.getCategoria()) + horas);
                cuenta.put(i.getCategoria(), cuenta.get(i.getCategoria()) + 1);
            }
        }

        Map<Incidencia.Categoria, Double> res = new EnumMap<>(Incidencia.Categoria.class);
        for (Incidencia.Categoria c : Incidencia.Categoria.values()) {
            if (cuenta.get(c) == 0) res.put(c, 0.0);
            else res.put(c, suma.get(c) / (double) cuenta.get(c));
        }

        return res;
    }
//---------------------
    public Map<Incidencia.Prioridad, Long> resumenAbiertasPorPrioridad() {
        Map<Incidencia.Prioridad, Long> resultado = new HashMap<>();

        for (Incidencia inc : incidencias) {
            if (inc.getEstado() == Incidencia.Estado.ABIERTA) {
                Incidencia.Prioridad p = inc.getPrioridad();
                resultado.put(p, resultado.getOrDefault(p, 0L) + 1);
            }
        }
        return resultado;
    }

    public List<Incidencia> alertasCriticas() {
        List<Incidencia> resultado = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        for (Incidencia inc : incidencias) {

            boolean esCritica = inc.getPrioridad() == Incidencia.Prioridad.CRITICA;

            boolean estaAbierta =
                    inc.getEstado() == Incidencia.Estado.ABIERTA ||
                    inc.getEstado() == Incidencia.Estado.EN_CURSO ||
                    inc.getEstado() == Incidencia.Estado.EN_ESPERA;

            boolean masDe4h =
                    java.time.Duration.between(
                            inc.getFechaApertura(),
                            ahora
                    ).toHours() >= 4;

            if (esCritica && estaAbierta && masDe4h) {
                resultado.add(inc);
            }
        }

        return resultado;
    }

    public Map<Incidencia.Categoria, Double> mediaHorasResolucionPorCategoria() {
        Map<Incidencia.Categoria, Long> sumaHoras = new HashMap<>();
        Map<Incidencia.Categoria, Integer> contador = new HashMap<>();

        for (Incidencia inc : incidencias) {
            if (inc.getFechaResolucion() != null) {
                long horas = java.time.Duration.between(
                        inc.getFechaApertura(),
                        inc.getFechaResolucion()
                ).toHours();

                Incidencia.Categoria cat = inc.getCategoria();

                sumaHoras.put(cat, sumaHoras.getOrDefault(cat, 0L) + horas);
                contador.put(cat, contador.getOrDefault(cat, 0) + 1);
            }
        }

        Map<Incidencia.Categoria, Double> resultado = new HashMap<>();
        for (Incidencia.Categoria cat : sumaHoras.keySet()) {
            double media = (double) sumaHoras.get(cat) / contador.get(cat);
            resultado.put(cat, media);
        }

        return resultado;
    }

    public List<Incidencia> incidenciasDelMesActual() {
        List<Incidencia> lista = new ArrayList<>();

        LocalDateTime inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fin = inicio.plusMonths(1);

        for (Incidencia inc : incidencias) {
            LocalDateTime fecha = inc.getFechaApertura();
            if (fecha.isAfter(inicio) && fecha.isBefore(fin)) {
                lista.add(inc);
            }
        }

        return lista;
    }

    public Map<String, Double> mediaHorasPorTecnico() {
        Map<String, Long> sumaHoras = new HashMap<>();
        Map<String, Integer> contador = new HashMap<>();

        for (Incidencia inc : incidencias) {

            // Debe tener técnico asignado y estar resuelta
            if (inc.getTecnicoAsignadoId() != null &&
                inc.getFechaResolucion() != null &&
                inc.getFechaApertura() != null) {

                // Buscar el técnico por ID
                Tecnico tecnico = null;
                for (Tecnico t : tecnicos) {
                    if (t.getId().equals(inc.getTecnicoAsignadoId())) {
                        tecnico = t;
                        break;
                    }
                }

                if (tecnico == null) continue; // si no existe, saltar

                long horas = java.time.Duration.between(
                        inc.getFechaApertura(),
                        inc.getFechaResolucion()
                ).toHours();

                String nombreTec = tecnico.getNombre();

                // Acumular
                if (!sumaHoras.containsKey(nombreTec)) {
                    sumaHoras.put(nombreTec, horas);
                    contador.put(nombreTec, 1);
                } else {
                    sumaHoras.put(nombreTec, sumaHoras.get(nombreTec) + horas);
                    contador.put(nombreTec, contador.get(nombreTec) + 1);
                }
            }
        }

        // Calcular medias
        Map<String, Double> resultado = new HashMap<>();
        for (String nombre : sumaHoras.keySet()) {
            double media = (double) sumaHoras.get(nombre) / contador.get(nombre);
            resultado.put(nombre, media);
        }

        return resultado;
    }


    // ─────────────────────────────────────────────────────────────
    // SLA
    // ─────────────────────────────────────────────────────────────

    public double porcentajeSLACriticas() {
        int total = 0;
        int ok = 0;

        for (Incidencia i : incidencias) {
            if (i.getPrioridad() == Incidencia.Prioridad.CRITICA &&
                i.getFechaResolucion() != null) {

                total++;

                long horas = Duration.between(i.getFechaApertura(), i.getFechaResolucion()).toHours();
                if (horas < 4) ok++;
            }
        }

        return total == 0 ? 0 : (ok * 100.0 / total);
    }

    // ─────────────────────────────────────────────────────────────
    // CRUD TÉCNICOS
    // ─────────────────────────────────────────────────────────────

    public void crearTecnico(Tecnico t) {
        validarTecnico(t);
        tecnicos.add(t);
        guardarTecnicos();
    }

    public void actualizarTecnico(Tecnico t) {
        validarTecnico(t);
        int idx = indexOfTecnico(t.getId());
        tecnicos.set(idx, t);
        guardarTecnicos();
    }

    public void eliminarTecnico(String id) {
        for (Incidencia i : incidencias) {
            if (id.equals(i.getTecnicoAsignadoId()) &&
                i.getEstado() != Estado.CERRADA &&
                i.getEstado() != Estado.RESUELTA) {
                throw new IllegalStateException("El técnico tiene incidencias activas asignadas.");
            }
        }

        for (int i = 0; i < tecnicos.size(); i++) {
            if (tecnicos.get(i).getId().equals(id)) {
                tecnicos.remove(i);
                break;
            }
        }

        guardarTecnicos();
    }

    public Optional<Tecnico> buscarTecnicoPorId(String id) {
        for (Tecnico t : tecnicos) {
            if (t.getId().equals(id)) return Optional.of(t);
        }
        return Optional.empty();
    }

    public List<Tecnico> listarTecnicos() {
        return new ArrayList<>(tecnicos);
    }

    public List<Tecnico> listarTecnicosActivos() {
        List<Tecnico> res = new ArrayList<>();
        for (Tecnico t : tecnicos) {
            if (t.isActivo()) res.add(t);
        }
        return res;
    }

    public void asignarTecnico(String incId, String tecId) {
        Incidencia inc = buscarIncidenciaPorId(incId)
                .orElseThrow(() -> new IllegalArgumentException("Incidencia no encontrada"));

        Tecnico tec = buscarTecnicoPorId(tecId)
                .orElseThrow(() -> new IllegalArgumentException("Técnico no encontrado"));

        if (!tec.isActivo()) {
            throw new IllegalStateException("No se puede asignar un técnico inactivo.");
        }

        inc.setTecnicoAsignadoId(tecId);

        if (inc.getEstado() == Estado.ABIERTA || inc.getEstado() == Estado.EN_ESPERA) {
            Estado anterior = inc.getEstado();
            inc.setEstado(Estado.EN_CURSO);
            registrarHistorial(incId, anterior, Estado.EN_CURSO, "Técnico asignado");
        }

        actualizarIncidencia(inc);
    }

    // ─────────────────────────────────────────────────────────────
    // VALIDACIONES
    // ─────────────────────────────────────────────────────────────

    private void validarIncidencia(Incidencia i) {
        if (isBlank(i.getTitulo())) throw new IllegalArgumentException("El título es obligatorio.");
        if (isBlank(i.getDescripcion())) throw new IllegalArgumentException("La descripción es obligatoria.");
        if (isBlank(i.getNombreSolicitante())) throw new IllegalArgumentException("El nombre del solicitante es obligatorio.");
        if (!emailValido(i.getEmailSolicitante())) throw new IllegalArgumentException("Email inválido.");
        if (i.getFechaApertura() == null) throw new IllegalArgumentException("Fecha de apertura obligatoria.");
        if (i.getFechaResolucion() != null &&
            i.getFechaResolucion().isBefore(i.getFechaApertura())) {
            throw new IllegalArgumentException("La fecha de resolución no puede ser anterior a la apertura.");
        }
    }

    private void validarTecnico(Tecnico t) {
        if (isBlank(t.getNombre())) throw new IllegalArgumentException("Nombre obligatorio.");
        if (isBlank(t.getApellidos())) throw new IllegalArgumentException("Apellidos obligatorios.");
        if (!emailValido(t.getEmailCorporativo())) throw new IllegalArgumentException("Email corporativo inválido.");
        if (t.getEspecialidad() == null) throw new IllegalArgumentException("Especialidad obligatoria.");
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }

    private boolean emailValido(String e) {
        return e != null && e.matches("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)+$");
    }

    private int indexOf(String id) {
        for (int i = 0; i < incidencias.size(); i++) {
            if (incidencias.get(i).getId().equals(id)) return i;
        }
        throw new NoSuchElementException("Incidencia no encontrada: " + id);
    }

    private int indexOfTecnico(String id) {
        for (int i = 0; i < tecnicos.size(); i++) {
            if (tecnicos.get(i).getId().equals(id)) return i;
        }
        throw new NoSuchElementException("Técnico no encontrado: " + id);
    }

    private void guardarIncidencias() { persistence.guardarIncidencias(incidencias); }
    private void guardarTecnicos()    { persistence.guardarTecnicos(tecnicos); }
}


