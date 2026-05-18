package com.helpdesk.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.helpdesk.model.enums.Categoria;
import com.helpdesk.model.enums.Estado;
import com.helpdesk.model.enums.Prioridad;

public class Incidencia {

    private int id;
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private Prioridad prioridad;
    private String solicitante;
    private String emailSolicitante;
    private Estado estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String solucion;

    private Integer idTecnico;
    private Tecnico tecnicoAsignado;

    // === HISTORIAL PROFESIONAL ===
    private List<RegistroHistorial> historial = new ArrayList<>();

    public Incidencia() {
        this.historial = new ArrayList<>();
    }

    public Incidencia(int id, String titulo, String descripcion, Categoria categoria,
                      Prioridad prioridad, String solicitante, String emailSolicitante) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.prioridad = prioridad;
        this.solicitante = solicitante;
        this.emailSolicitante = emailSolicitante;
        this.estado = Estado.ABIERTA;
        this.fechaApertura = LocalDateTime.now();

        // Registro inicial
        historial.add(new RegistroHistorial(null, Estado.ABIERTA, "Apertura del ticket"));
    }

    // === HISTORIAL ===
    public List<RegistroHistorial> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    public void registrarCambioEstado(Estado nuevoEstado, String motivo) {

        if (this.estado == Estado.CERRADA) {
            throw new IllegalStateException("No se puede modificar una incidencia cerrada.");
        }

        Estado anterior = this.estado;
        this.estado = nuevoEstado;

        // Fecha de cierre automática
        if (nuevoEstado == Estado.RESUELTA || nuevoEstado == Estado.CERRADA) {
            this.fechaCierre = LocalDateTime.now();
        }

        historial.add(new RegistroHistorial(anterior, nuevoEstado, motivo));
    }

    // === Cierre manual con solución ===
    public void cerrarIncidencia(String solucion) {
        this.solucion = solucion;
    }

    // === Getters y Setters (los tuyos, sin tocar) ===
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }

    public String getEmailSolicitante() { return emailSolicitante; }
    public void setEmailSolicitante(String emailSolicitante) { this.emailSolicitante = emailSolicitante; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public Tecnico getTecnicoAsignado() { return tecnicoAsignado; }
    public void setTecnicoAsignado(Tecnico tecnico) { this.tecnicoAsignado = tecnico; }

    public Integer getIdTecnico() { return idTecnico; }
    public void setIdTecnico(Integer idTecnico) { this.idTecnico = idTecnico; }

    public String getSolucion() { return solucion; }
    public void setSolucion(String solucion) { this.solucion = solucion; }

    public void setHistorial(List<RegistroHistorial> historial) {
    this.historial = (historial == null) ? new ArrayList<>() : historial;
}


    // === Tiempo de resolución ===
    public String getTiempoResolucion() {
        if (fechaCierre == null) return "Aún no está cerrada";
        Duration dur = Duration.between(fechaApertura, fechaCierre);
        long dias = dur.toDays();
        long horas = dur.minusDays(dias).toHours();
        return dias + " días y " + horas + " horas";
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return "\nID: " + id +
                "\nTítulo: " + titulo +
                "\nDescripción: " + descripcion +
                "\nCategoría: " + categoria +
                "\nPrioridad: " + prioridad +
                "\nSolicitante: " + solicitante +
                "\nEmail: " + emailSolicitante +
                "\nEstado: " + estado +
                "\nFecha apertura: " + fechaApertura.format(fmt) +
                (fechaCierre != null ? "\nFecha cierre: " + fechaCierre.format(fmt) : "") +
                (tecnicoAsignado != null ?
                "\nTécnico asignado: " + tecnicoAsignado.getNombre() + " " + tecnicoAsignado.getApellidos()
                : "\nTécnico asignado: (sin asignar)") +
                (solucion != null ? "\nSolución: " + solucion : "") +
                (fechaCierre != null ? "\nTiempo de resolución: " + getTiempoResolucion() : "") +
                "\n-----------------------------";
    }
}
