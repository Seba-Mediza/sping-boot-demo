package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Una venta junto con su importe (cantidad × precioUnitario)")
public record VentaConImporte(
        @Schema(example = "Mouse inalambrico") String producto,
        @Schema(example = "3") int cantidad,
        @Schema(example = "4500.0") double precioUnitario,
        @Schema(example = "13500.0") double importe) {
}
