package com.aydsii.calculadora.exception;

public class LibroNoEncontradoException extends RuntimeException {

    public LibroNoEncontradoException(String isbn) {
        super("No existe un libro con ISBN " + isbn);
    }
}
