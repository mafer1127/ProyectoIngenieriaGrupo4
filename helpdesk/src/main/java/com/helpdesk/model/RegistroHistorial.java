package com.helpdesk.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.helpdesk.model.enums.Estado;

public class RegistroHistorial {

    private final LocalDateTime fechaCambio;
    private final Estado estadoAnterior;
    private final Estado estadoNuevo;
    private final String usuario;

    public RegistroHistorial(Estado anterior, Estado nuevo, String usuario) {
        this.fechaCambio = LocalDateTime.now();
        this.estadoAnterior = anterior;
        this.estadoNuevo = nuevo;
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public Estado getEstadoAnterior() { return estadoAnterior; }
    public Estado getEstadoNuevo() { return estadoNuevo; }
    public String getUsuario() { return usuario; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %s -> %s | Usuario: %s",
                fechaCambio.format(fmt),
                estadoAnterior == null ? "CREACIÓN" : estadoAnterior,
                estadoNuevo,
                usuario);
    }
}
