package com.helpdesk.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Incidencia {

    public enum Categoria { HARDWARE, SOFTWARE, RED, ACCESO, OTRO }
    public enum Prioridad { BAJA, MEDIA, ALTA, CRITICA }
    public enum Estado    { ABIERTA, EN_CURSO, EN_ESPERA, RESUELTA, CERRADA }

    private String id;
    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private Prioridad prioridad;
    private String nombreSolicitante;
    private String emailSolicitante;
    private String creadoPorUsuarioId;
    private String tecnicoAsignadoId;
    private Estado estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaResolucion;
    private String descripcionSolucion;

    public Incidencia() {}

    public Incidencia(String titulo, String descripcion, Categoria categoria,
                      Prioridad prioridad, String nombreSolicitante, String emailSolicitante) {
        this.id                = UUID.randomUUID().toString();
        this.titulo            = titulo;
        this.descripcion       = descripcion;
        this.categoria         = categoria;
        this.prioridad         = prioridad;
        this.nombreSolicitante = nombreSolicitante;
        this.emailSolicitante  = emailSolicitante;
        this.estado            = Estado.ABIERTA;
        this.fechaApertura     = LocalDateTime.now();
    }

    public long getDiasResolucion() {
        if (fechaApertura == null || fechaResolucion == null) return -1;
        return java.time.Duration.between(fechaApertura, fechaResolucion).toDays();
    }

    public long getHorasResolucion() {
        if (fechaApertura == null || fechaResolucion == null) return -1;
        return java.time.Duration.between(fechaApertura, fechaResolucion).toHours() % 24;
    }

    public String getTiempoResolucionFormateado() {
        long d = getDiasResolucion();
        if (d < 0) return "—";
        return d + "d " + getHorasResolucion() + "h";
    }

    public boolean esAlertaCritica() {
        if (prioridad != Prioridad.CRITICA) return false;
        if (estado == Estado.RESUELTA || estado == Estado.CERRADA) return false;
        if (fechaApertura == null) return false;
        return java.time.Duration.between(fechaApertura, LocalDateTime.now()).toHours() >= 4;
    }

    /** SLA: crítica resuelta en menos de 4h */
    public boolean cumpleSLA() {
        if (prioridad != Prioridad.CRITICA) return true;
        if (fechaResolucion == null || fechaApertura == null) return false;
        return java.time.Duration.between(fechaApertura, fechaResolucion).toHours() < 4;
    }

    public String getId()                              { return id; }
    public void   setId(String id)                     { this.id = id; }
    public String getTitulo()                          { return titulo; }
    public void   setTitulo(String titulo)             { this.titulo = titulo; }
    public String getDescripcion()                     { return descripcion; }
    public void   setDescripcion(String d)             { this.descripcion = d; }
    public Categoria getCategoria()                    { return categoria; }
    public void   setCategoria(Categoria c)            { this.categoria = c; }
    public Prioridad getPrioridad()                    { return prioridad; }
    public void   setPrioridad(Prioridad p)            { this.prioridad = p; }
    public String getNombreSolicitante()               { return nombreSolicitante; }
    public void   setNombreSolicitante(String n)       { this.nombreSolicitante = n; }
    public String getEmailSolicitante()                { return emailSolicitante; }
    public void   setEmailSolicitante(String e)        { this.emailSolicitante = e; }
    public String getCreadoPorUsuarioId()              { return creadoPorUsuarioId; }
    public void   setCreadoPorUsuarioId(String id)     { this.creadoPorUsuarioId = id; }
    public String getTecnicoAsignadoId()               { return tecnicoAsignadoId; }
    public void   setTecnicoAsignadoId(String t)       { this.tecnicoAsignadoId = t; }
    public Estado getEstado()                          { return estado; }
    public void   setEstado(Estado e)                  { this.estado = e; }
    public LocalDateTime getFechaApertura()            { return fechaApertura; }
    public void   setFechaApertura(LocalDateTime f)    { this.fechaApertura = f; }
    public LocalDateTime getFechaResolucion()          { return fechaResolucion; }
    public void   setFechaResolucion(LocalDateTime f)  { this.fechaResolucion = f; }
    public String getDescripcionSolucion()             { return descripcionSolucion; }
    public void   setDescripcionSolucion(String s)     { this.descripcionSolucion = s; }
}
