package com.aydsii.calculadora.exception;

import java.util.List;

import com.aydsii.calculadora.dto.ErrorValidacion;

/**
 * Uno o más elementos de una lista recibida no cumplen las validaciones. A diferencia de
 * MethodArgumentNotValidException (que no permite recuperar de forma confiable la posición
 * de cada elemento de una List validada por cascada), esta excepción ya trae el detalle
 * armado: posición, campo y motivo de cada error.
 */
public class ValidacionListaException extends RuntimeException {

    private final List<ErrorValidacion> errores;

    /** Mensaje simple, por ejemplo cuando la lista completa no puede estar vacía (sin detalle por elemento). */
    public ValidacionListaException(String mensaje) {
        super(mensaje);
        this.errores = null;
    }

    /** Uno o más elementos de la lista son inválidos; el detalle ya trae posición, campo y motivo. */
    public ValidacionListaException(List<ErrorValidacion> errores) {
        super("Error de validación");
        this.errores = errores;
    }

    public List<ErrorValidacion> getErrores() {
        return errores;
    }
}
