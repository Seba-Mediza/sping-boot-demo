package com.aydsii.calculadora.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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

    /**
     * Devuelve los libros que cumplen todos los filtros indicados (los null se ignoran).
     * Los filtros de texto son "contiene", sin distinguir mayúsculas. El resultado se
     * puede ordenar por un campo (ordenarPor) de forma ascendente o descendente.
     */
    public List<Libro> buscar(String titulo, String autor, String genero, Boolean soloDisponibles,
            String ordenarPor, boolean descendente) {
        // Stream sobre el catálogo, encadenando un filter() por cada filtro opcional.
        Stream<Libro> stream = storage.listarTodos().stream()
                .filter(libro -> titulo == null || contiene(libro.getTitulo(), titulo))
                .filter(libro -> autor == null || contiene(libro.getAutor(), autor))
                .filter(libro -> genero == null || contiene(libro.getGenero(), genero))
                .filter(libro -> !Boolean.TRUE.equals(soloDisponibles) || libro.getCantidadDisponibles() > 0);

        // Si se pidió un criterio de orden, construimos el Comparator y hacemos el sort con sorted().
        Comparator<Libro> comparator = comparatorPara(ordenarPor);
        if (comparator != null) {
            stream = stream.sorted(descendente ? comparator.reversed() : comparator);
        }

        return stream.toList();
    }

    private boolean contiene(String valor, String filtro) {
        return valor != null && valor.toLowerCase().contains(filtro.toLowerCase());
    }

    /** Devuelve el Comparator correspondiente al campo pedido, o null si no hay que ordenar. */
    private Comparator<Libro> comparatorPara(String ordenarPor) {
        if (ordenarPor == null) {
            return null;
        }
        return switch (ordenarPor.toLowerCase()) {
            case "titulo" -> Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER);
            case "autor" -> Comparator.comparing(Libro::getAutor, String.CASE_INSENSITIVE_ORDER);
            case "genero" -> Comparator.comparing(Libro::getGenero, String.CASE_INSENSITIVE_ORDER);
            case "cantidaddisponibles" -> Comparator.comparingInt(Libro::getCantidadDisponibles);
            default -> throw new IllegalArgumentException(
                    "No se puede ordenar por '" + ordenarPor + "'. Valores válidos: titulo, autor, genero, cantidadDisponibles");
        };
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
