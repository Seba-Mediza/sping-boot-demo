package com.aydsii.calculadora.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Producto del catálogo en memoria (TP Spring, Ejercicio 2). No es una entidad JPA:
 * vive en una List dentro de CatalogoService, sin base de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Producto del catálogo en memoria")
public class ProductoCatalogo {

    @Schema(description = "Identificador asignado en memoria", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Mouse inalambrico")
    private String nombre;

    @Schema(description = "Categoría del producto", example = "Perifericos")
    private String categoria;

    @Schema(description = "Precio del producto", example = "4500.0")
    private double precio;

    @Schema(description = "Cantidad disponible en stock", example = "15")
    private int stock;
}
