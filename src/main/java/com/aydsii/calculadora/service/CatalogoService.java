package com.aydsii.calculadora.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.aydsii.calculadora.dto.ProductoDTO;
import com.aydsii.calculadora.exception.ProductoNoEncontradoException;
import com.aydsii.calculadora.exception.StockInvalidoException;
import com.aydsii.calculadora.model.ProductoCatalogo;

import jakarta.annotation.PostConstruct;

/**
 * Catálogo de productos en memoria (TP Spring, Ejercicio 2). No usa base de datos:
 * la colección vive en esta clase durante la vida del proceso.
 */
@Service
public class CatalogoService {

    private final List<ProductoCatalogo> productos = new ArrayList<>();
    private final AtomicLong secuenciaId = new AtomicLong();

    @PostConstruct
    public void cargarDatosIniciales() {
        agregar(new ProductoDTO("Mouse inalambrico", "Perifericos", 4500.0, 15));
        agregar(new ProductoDTO("Teclado mecanico", "Perifericos", 12000.0, 8));
        agregar(new ProductoDTO("Monitor 24 pulgadas", "Computacion", 85000.0, 5));
        agregar(new ProductoDTO("Notebook Core i5", "Computacion", 450000.0, 3));
        agregar(new ProductoDTO("Auriculares Bluetooth", "Audio", 18000.0, 12));
        agregar(new ProductoDTO("Parlante portatil", "Audio", 22000.0, 7));
        agregar(new ProductoDTO("Webcam Full HD", "Perifericos", 15000.0, 10));
        agregar(new ProductoDTO("Disco SSD 480GB", "Computacion", 32000.0, 20));
    }

    public List<ProductoCatalogo> listarTodos() {
        return new ArrayList<>(productos);
    }

    /** Filtra con Streams (filter()); cada filtro es opcional y se combinan con AND. */
    public List<ProductoCatalogo> buscar(String categoria, Double precioMin, Double precioMax) {
        return productos.stream()
                .filter(producto -> categoria == null || producto.getCategoria().equalsIgnoreCase(categoria))
                .filter(producto -> precioMin == null || producto.getPrecio() >= precioMin)
                .filter(producto -> precioMax == null || producto.getPrecio() <= precioMax)
                .toList();
    }

    /** Ordena con Streams y Comparator según criterio ("precio" o "nombre") y orden ("asc"/"desc"). */
    public List<ProductoCatalogo> ordenar(String criterio, String orden) {
        Comparator<ProductoCatalogo> comparator = switch (criterio) {
            case "precio" -> Comparator.comparingDouble(ProductoCatalogo::getPrecio);
            case "nombre" -> Comparator.comparing(ProductoCatalogo::getNombre, String.CASE_INSENSITIVE_ORDER);
            default -> throw new IllegalArgumentException(
                    "criterio inválido: '" + criterio + "'. Valores válidos: precio, nombre");
        };
        if ("desc".equals(orden)) {
            comparator = comparator.reversed();
        }
        return productos.stream().sorted(comparator).toList();
    }

    public ProductoCatalogo agregar(ProductoDTO datos) {
        ProductoCatalogo producto = new ProductoCatalogo(secuenciaId.incrementAndGet(), datos.nombre(),
                datos.categoria(), datos.precio(), datos.stock());
        productos.add(producto);
        return producto;
    }

    public ProductoCatalogo modificarStock(Long id, int cantidad) {
        ProductoCatalogo producto = buscarPorId(id);
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new StockInvalidoException(
                    "El stock no puede quedar por debajo de 0 (stock actual: " + producto.getStock()
                            + ", modificación: " + cantidad + ")");
        }
        producto.setStock(nuevoStock);
        return producto;
    }

    public void eliminar(Long id) {
        ProductoCatalogo producto = buscarPorId(id);
        productos.remove(producto);
    }

    private ProductoCatalogo buscarPorId(Long id) {
        return productos.stream()
                .filter(producto -> producto.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }
}
