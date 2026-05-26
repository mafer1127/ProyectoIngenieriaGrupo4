package com.helpdesk.model;

import java.time.LocalDateTime;

public class HistorialCambio {

    private String        incidenciaId;
    private Incidencia.Estado estadoAnterior;
    private Incidencia.Estado estadoNuevo;
    private String        usuario;
    private String        nota;
    private LocalDateTime fecha;

    public HistorialCambio() {}

    public HistorialCambio(String incidenciaId, Incidencia.Estado anterior,
                           Incidencia.Estado nuevo, String usuario, String nota) {
        this.incidenciaId   = incidenciaId;
        this.estadoAnterior = anterior;
        this.estadoNuevo    = nuevo;
        this.usuario        = usuario;
        this.nota           = nota;
        this.fecha          = LocalDateTime.now();
    }

    public String getIncidenciaId()                    { return incidenciaId; }
    public void   setIncidenciaId(String id)           { this.incidenciaId = id; }
    public Incidencia.Estado getEstadoAnterior()       { return estadoAnterior; }
    public void   setEstadoAnterior(Incidencia.Estado e){ this.estadoAnterior = e; }
    public Incidencia.Estado getEstadoNuevo()          { return estadoNuevo; }
    public void   setEstadoNuevo(Incidencia.Estado e)  { this.estadoNuevo = e; }
    public String getUsuario()                         { return usuario; }
    public void   setUsuario(String u)                 { this.usuario = u; }
    public String getNota()                            { return nota; }
    public void   setNota(String n)                    { this.nota = n; }
    public LocalDateTime getFecha()                    { return fecha; }
    public void   setFecha(LocalDateTime f)            { this.fecha = f; }
}
