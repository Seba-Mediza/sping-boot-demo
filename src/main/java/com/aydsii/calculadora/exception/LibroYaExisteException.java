package com.aydsii.calculadora.exception;

public class LibroYaExisteException extends RuntimeException {

    public LibroYaExisteException(String isbn) {
        super("Ya existe un libro con ISBN " + isbn);
    }
}
