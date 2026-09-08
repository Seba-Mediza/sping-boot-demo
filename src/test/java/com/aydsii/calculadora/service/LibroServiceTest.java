package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aydsii.calculadora.Storage;
import com.aydsii.calculadora.exception.LibroNoEncontradoException;
import com.aydsii.calculadora.exception.LibroYaExisteException;
import com.aydsii.calculadora.model.Libro;

class LibroServiceTest {

    private LibroService libroService;

    @BeforeEach
    void setUp() {
        Storage storage = new Storage();
        storage.cargarDatosIniciales();
        libroService = new LibroService(storage);
    }

    @Test
    void listarTodosDevuelveLosLibrosPrecargados() {
        assertThat(libroService.listarTodos()).hasSize(5);
    }

    @Test
    void buscarSinFiltrosDevuelveTodos() {
        assertThat(libroService.buscar(null, null, null, null, null, false)).hasSize(5);
    }

    @Test
    void buscarPorTituloEsParcialYSinDistinguirMayusculas() {
        assertThat(libroService.buscar("años", null, null, null, null, false))
                .extracting(Libro::getTitulo)
                .containsExactly("Cien años de soledad");
    }

    @Test
    void buscarPorAutorYGeneroCombinaLosFiltros() {
        assertThat(libroService.buscar(null, "orwell", "distopía", null, null, false))
                .extracting(Libro::getIsbn)
                .containsExactly("978-0451524935");
    }

    @Test
    void buscarConSoloDisponiblesExcluyeLosQueTienenCantidadCero() {
        libroService.actualizar("978-0451524935",
                new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 0));

        assertThat(libroService.buscar(null, null, null, true, null, false))
                .extracting(Libro::getIsbn)
                .doesNotContain("978-0451524935")
                .hasSize(4);
    }

    @Test
    void buscarSinResultadosDevuelveListaVacia() {
        assertThat(libroService.buscar("no-existe", null, null, null, null, false)).isEmpty();
    }

    @Test
    void buscarOrdenaPorTituloAscendente() {
        assertThat(libroService.buscar(null, null, null, null, "titulo", false))
                .extracting(Libro::getTitulo)
                .containsExactly("1984", "Cien años de soledad", "El nombre del viento",
                        "El principito", "Fahrenheit 451");
    }

    @Test
    void buscarOrdenaPorCantidadDisponiblesDescendente() {
        assertThat(libroService.buscar(null, null, null, null, "cantidadDisponibles", true))
                .extracting(Libro::getCantidadDisponibles)
                .containsExactly(10, 8, 5, 3, 2);
    }

    @Test
    void buscarConCampoDeOrdenInvalidoLanzaIllegalArgumentException() {
        assertThatThrownBy(() -> libroService.buscar(null, null, null, null, "paginas", false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void obtenerDevuelveElLibroConEseIsbn() {
        Libro libro = libroService.obtener("978-0451524935");

        assertThat(libro.getTitulo()).isEqualTo("1984");
        assertThat(libro.getAutor()).isEqualTo("George Orwell");
    }

    @Test
    void obtenerConIsbnInexistenteLanzaLibroNoEncontradoException() {
        assertThatThrownBy(() -> libroService.obtener("000"))
                .isInstanceOf(LibroNoEncontradoException.class)
                .hasMessage("No existe un libro con ISBN 000");
    }

    @Test
    void crearAgregaUnLibroNuevoAlCatalogo() {
        Libro nuevo = new Libro("Rayuela", "Julio Cortázar", "978-8437604572", "Novela", 4);

        libroService.crear(nuevo);

        assertThat(libroService.listarTodos()).hasSize(6);
        assertThat(libroService.obtener("978-8437604572").getTitulo()).isEqualTo("Rayuela");
    }

    @Test
    void crearConIsbnExistenteLanzaLibroYaExisteException() {
        Libro duplicado = new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 1);

        assertThatThrownBy(() -> libroService.crear(duplicado))
                .isInstanceOf(LibroYaExisteException.class);
    }

    @Test
    void actualizarModificaLosDatosDelLibroExistente() {
        Libro datos = new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 20);

        Libro actualizado = libroService.actualizar("978-0451524935", datos);

        assertThat(actualizado.getCantidadDisponibles()).isEqualTo(20);
        assertThat(libroService.obtener("978-0451524935").getCantidadDisponibles()).isEqualTo(20);
    }

    @Test
    void actualizarConIsbnInexistenteLanzaLibroNoEncontradoException() {
        Libro datos = new Libro("X", "Y", "000", "Z", 1);

        assertThatThrownBy(() -> libroService.actualizar("000", datos))
                .isInstanceOf(LibroNoEncontradoException.class);
    }

    @Test
    void eliminarQuitaElLibroDelCatalogo() {
        libroService.eliminar("978-0451524935");

        assertThat(libroService.listarTodos()).hasSize(4);
        assertThatThrownBy(() -> libroService.obtener("978-0451524935"))
                .isInstanceOf(LibroNoEncontradoException.class);
    }

    @Test
    void eliminarConIsbnInexistenteLanzaLibroNoEncontradoException() {
        assertThatThrownBy(() -> libroService.eliminar("000"))
                .isInstanceOf(LibroNoEncontradoException.class);
    }
}
