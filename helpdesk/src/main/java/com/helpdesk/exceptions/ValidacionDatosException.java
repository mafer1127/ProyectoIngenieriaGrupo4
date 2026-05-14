package com.helpdesk.exceptions;

public class ValidacionDatosException extends RuntimeException {
    public ValidacionDatosException(String mensaje) {
        super(mensaje);
    }
}