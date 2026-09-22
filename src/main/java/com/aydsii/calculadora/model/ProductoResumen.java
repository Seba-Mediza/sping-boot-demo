package com.aydsii.calculadora.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Producto incluido en un pedido")
public record ProductoResumen(
        @Schema(example = "Mouse inalambrico") String nombre,
        @Schema(example = "Perifericos") String categoria,
        @Schema(example = "2") int cantidad,
        @Schema(example = "9000.0") double subtotal) {
}
