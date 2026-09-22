package com.aydsii.calculadora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.dto.ApiResponse;
import com.aydsii.calculadora.dto.ProductoDTO;
import com.aydsii.calculadora.model.ProductoCatalogo;
import com.aydsii.calculadora.service.CatalogoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

// @RestController: controlador REST del catálogo en memoria (TP Spring, Ejercicio 2).
@RestController
@RequestMapping("/api/catalogo")
@Tag(name = "TP Spring - Catálogo", description = "Catálogo de productos en memoria: consulta, búsqueda, orden y CRUD")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    @Operation(summary = "Lista todos los productos", description = "Devuelve todos los productos disponibles en el catálogo.")
    public ResponseEntity<ApiResponse<List<ProductoCatalogo>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.of(HttpStatus.OK.value(), "Operación realizada con éxito", catalogoService.listarTodos()));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca productos con filtros opcionales",
            description = "categoria, precioMin y precioMax son opcionales y se combinan con AND. Sin ninguno, "
                    + "devuelve todo el catálogo.")
    public ResponseEntity<ApiResponse<List<ProductoCatalogo>>> buscar(
            @Parameter(description = "Filtra por categoría exacta") @RequestParam(required = false) String categoria,
            @Parameter(description = "Precio mínimo (inclusive)") @RequestParam(required = false) Double precioMin,
            @Parameter(description = "Precio máximo (inclusive)") @RequestParam(required = false) Double precioMax) {
        List<ProductoCatalogo> resultado = catalogoService.buscar(categoria, precioMin, precioMax);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Operación realizada con éxito", resultado));
    }

    @GetMapping("/ordenar")
    @Operation(summary = "Ordena el catálogo por precio o nombre",
            description = "criterio: precio o nombre. orden: asc (por defecto) o desc.")
    public ResponseEntity<ApiResponse<List<ProductoCatalogo>>> ordenar(
            @Parameter(description = "Campo por el que ordenar", example = "precio")
            @RequestParam @Pattern(regexp = "precio|nombre",
                    message = "criterio inválido: debe ser 'precio' o 'nombre'") String criterio,
            @Parameter(description = "Dirección del orden", example = "desc")
            @RequestParam(defaultValue = "asc") @Pattern(regexp = "asc|desc",
                    message = "orden inválido: debe ser 'asc' o 'desc'") String orden) {
        List<ProductoCatalogo> resultado = catalogoService.ordenar(criterio, orden);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Operación realizada con éxito", resultado));
    }

    @PostMapping
    @Operation(summary = "Agrega un producto nuevo al catálogo")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Producto creado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ApiResponse<ProductoCatalogo>> agregar(@Valid @RequestBody ProductoDTO datos) {
        ProductoCatalogo creado = catalogoService.agregar(datos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Producto creado correctamente", creado));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Modifica el stock de un producto",
            description = "cantidad puede ser positiva (aumenta) o negativa (disminuye). El stock nunca puede quedar "
                    + "por debajo de 0.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock actualizado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "La modificación dejaría el stock en negativo"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No existe un producto con ese id")
    })
    public ResponseEntity<ApiResponse<ProductoCatalogo>> modificarStock(
            @Parameter(description = "Id del producto") @PathVariable Long id,
            @Parameter(description = "Cuánto modificar el stock actual (puede ser negativo)", example = "5")
            @RequestParam int cantidad) {
        ProductoCatalogo actualizado = catalogoService.modificarStock(id, cantidad);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), "Stock actualizado correctamente", actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un producto del catálogo")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Producto eliminado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No existe un producto con ese id")
    })
    public ResponseEntity<ApiResponse<Object>> eliminar(@Parameter(description = "Id del producto") @PathVariable Long id) {
        catalogoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Producto eliminado correctamente", null));
    }
}
