package com.aydsii.calculadora;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.aydsii.calculadora.model.Libro;

import jakarta.annotation.PostConstruct;

/**
 * "Base de datos" en memoria. Guarda los libros en un Map indexado por ISBN y se
 * precarga con algunos registros al levantar el proyecto.
 */
// @Component: registra esta clase como bean de Spring, para poder inyectarla (aquí, en LibroService).
@Component
public class Storage {

    private final Map<String, Libro> libros = new LinkedHashMap<>();

    // @PostConstruct: Spring ejecuta este método una vez, apenas crea el bean, al levantar el proyecto.
    @PostConstruct
    public void cargarDatosIniciales() {
        guardar(new Libro("Cien años de soledad", "Gabriel García Márquez", "978-0307474728", "Realismo mágico", 5));
        guardar(new Libro("El nombre del viento", "Patrick Rothfuss", "978-8401352836", "Fantasía", 3));
        guardar(new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 8));
        guardar(new Libro("Fahrenheit 451", "Ray Bradbury", "978-1451673319", "Ciencia ficción", 2));
        guardar(new Libro("El principito", "Antoine de Saint-Exupéry", "978-0156012195", "Fábula", 10));
    }

    public List<Libro> listarTodos() {
        return new ArrayList<>(libros.values());
    }

    public Optional<Libro> buscarPorIsbn(String isbn) {
        return Optional.ofNullable(libros.get(isbn));
    }

    public boolean existe(String isbn) {
        return libros.containsKey(isbn);
    }

    public Libro guardar(Libro libro) {
        libros.put(libro.getIsbn(), libro);
        return libro;
    }

    public void eliminar(String isbn) {
        libros.remove(isbn);
    }
}
