package com.aydsii.calculadora.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aydsii.calculadora.Storage;
import com.aydsii.calculadora.exception.LibroNoEncontradoException;
import com.aydsii.calculadora.exception.LibroYaExisteException;
import com.aydsii.calculadora.model.Libro;

// @Service: bean de Spring que contiene la lógica de negocio; se inyecta en el controller.
@Service
public class LibroService {

    private final Storage storage;

    public LibroService(Storage storage) {
        this.storage = storage;
    }

    public List<Libro> listarTodos() {
        return storage.listarTodos();
    }

    public Libro obtener(String isbn) {
        return storage.buscarPorIsbn(isbn)
                .orElseThrow(() -> new LibroNoEncontradoException(isbn));
    }

    public Libro crear(Libro libro) {
        if (storage.existe(libro.getIsbn())) {
            throw new LibroYaExisteException(libro.getIsbn());
        }
        return storage.guardar(libro);
    }

    public Libro actualizar(String isbn, Libro datos) {
        Libro existente = obtener(isbn);
        existente.setTitulo(datos.getTitulo());
        existente.setAutor(datos.getAutor());
        existente.setGenero(datos.getGenero());
        existente.setCantidadDisponibles(datos.getCantidadDisponibles());
        return storage.guardar(existente);
    }

    public void eliminar(String isbn) {
        obtener(isbn);
        storage.eliminar(isbn);
    }
}
