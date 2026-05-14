package com.helpdesk.exceptions;

public class TecnicoNoDisponibleException extends RuntimeException {
    public TecnicoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}