package com.helpdesk.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private Tecnico tecnicoAsignado;

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
    }

    // Getters y setters
    public int getId() { return id; }
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
    public String getEmailSolicitante() { return emailSolicitante; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }

    public void setTecnicoAsignado(Tecnico tecnico) { this.tecnicoAsignado = tecnico;}
    public Tecnico getTecnicoAsignado() { return tecnicoAsignado;}

    public void cerrarIncidencia() {
        this.estado = Estado.CERRADA;
        this.fechaCierre = LocalDateTime.now();
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
                "\n-----------------------------";
    }
}
