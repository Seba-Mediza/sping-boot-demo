package com.aydsii.calculadora.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException() {
        super("El email ya está registrado");
    }
}
