package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Una venta con el monto ya calculado luego de aplicar el descuento")
public record VentaConDescuento(
        @Schema(example = "Mouse inalambrico") String producto,
        @Schema(example = "3") int cantidad,
        @Schema(example = "4500.0") double precioUnitario,
        @Schema(example = "12150.0") double montoConDescuento) {
}
