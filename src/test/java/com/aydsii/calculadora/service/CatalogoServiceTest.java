package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aydsii.calculadora.dto.ProductoDTO;
import com.aydsii.calculadora.exception.ProductoNoEncontradoException;
import com.aydsii.calculadora.exception.StockInvalidoException;
import com.aydsii.calculadora.model.ProductoCatalogo;

class CatalogoServiceTest {

    private CatalogoService catalogoService;

    @BeforeEach
    void setUp() {
        catalogoService = new CatalogoService();
        catalogoService.cargarDatosIniciales();
    }

    @Test
    void cargaAlMenosOchoProductosDeEjemploAlIniciar() {
        assertThat(catalogoService.listarTodos()).hasSizeGreaterThanOrEqualTo(8);
    }

    @Test
    void buscarPorCategoriaDevuelveSoloEsaCategoria() {
        List<ProductoCatalogo> resultado = catalogoService.buscar("Audio", null, null);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).allMatch(producto -> producto.getCategoria().equals("Audio"));
    }

    @Test
    void buscarCombinaCategoriaYRangoDePrecioConAnd() {
        List<ProductoCatalogo> resultado = catalogoService.buscar("Perifericos", 5000.0, 20000.0);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado).allMatch(producto -> producto.getCategoria().equals("Perifericos")
                && producto.getPrecio() >= 5000.0 && producto.getPrecio() <= 20000.0);
    }

    @Test
    void buscarSinFiltrosDevuelveTodoElCatalogo() {
        assertThat(catalogoService.buscar(null, null, null)).hasSize(catalogoService.listarTodos().size());
    }

    @Test
    void ordenarPorPrecioAscendenteQuedaOrdenado() {
        List<ProductoCatalogo> resultado = catalogoService.ordenar("precio", "asc");

        for (int i = 1; i < resultado.size(); i++) {
            assertThat(resultado.get(i).getPrecio()).isGreaterThanOrEqualTo(resultado.get(i - 1).getPrecio());
        }
    }

    @Test
    void ordenarPorNombreDescendenteQuedaOrdenado() {
        List<ProductoCatalogo> resultado = catalogoService.ordenar("nombre", "desc");

        for (int i = 1; i < resultado.size(); i++) {
            assertThat(resultado.get(i).getNombre().compareToIgnoreCase(resultado.get(i - 1).getNombre()))
                    .isLessThanOrEqualTo(0);
        }
    }

    @Test
    void ordenarConCriterioInvalidoLanzaIllegalArgumentException() {
        assertThatThrownBy(() -> catalogoService.ordenar("color", "asc"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void agregarSumaUnProductoNuevoConIdAsignado() {
        int tamanioPrevio = catalogoService.listarTodos().size();

        ProductoCatalogo creado = catalogoService.agregar(new ProductoDTO("Teclado", "Perifericos", 25000, 10));

        assertThat(creado.getId()).isNotNull();
        assertThat(catalogoService.listarTodos()).hasSize(tamanioPrevio + 1);
    }

    @Test
    void modificarStockAumentaConCantidadPositiva() {
        ProductoCatalogo producto = catalogoService.listarTodos().get(0);
        int stockPrevio = producto.getStock();

        ProductoCatalogo actualizado = catalogoService.modificarStock(producto.getId(), 5);

        assertThat(actualizado.getStock()).isEqualTo(stockPrevio + 5);
    }

    @Test
    void modificarStockQueDejariaNegativoLanzaStockInvalidoException() {
        ProductoCatalogo producto = catalogoService.listarTodos().get(0);

        assertThatThrownBy(() -> catalogoService.modificarStock(producto.getId(), -(producto.getStock() + 1)))
                .isInstanceOf(StockInvalidoException.class);
    }

    @Test
    void modificarStockDeProductoInexistenteLanzaProductoNoEncontradoException() {
        assertThatThrownBy(() -> catalogoService.modificarStock(999L, 1))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }

    @Test
    void eliminarQuitaElProductoDelCatalogo() {
        ProductoCatalogo producto = catalogoService.listarTodos().get(0);

        catalogoService.eliminar(producto.getId());

        assertThat(catalogoService.buscar(null, null, null))
                .noneMatch(p -> p.getId().equals(producto.getId()));
    }

    @Test
    void eliminarProductoInexistenteLanzaProductoNoEncontradoException() {
        assertThatThrownBy(() -> catalogoService.eliminar(999L))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }
}
