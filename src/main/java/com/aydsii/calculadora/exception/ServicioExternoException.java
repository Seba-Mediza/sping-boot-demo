package com.aydsii.calculadora.exception;

/**
 * Se lanza cuando la aplicación no puede obtener la información del servicio
 * externo (no responde, timeout, error HTTP, respuesta vacía, etc.).
 * El GlobalExceptionHandler la traduce a HTTP 502 (Bad Gateway).
 */
public class ServicioExternoException extends RuntimeException {

    public ServicioExternoException(String message) {
        super(message);
    }

    public ServicioExternoException(String message, Throwable cause) {
        super(message, cause);
    }
}
