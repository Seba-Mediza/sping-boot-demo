package com.aydsii.calculadora.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ventas con su monto con descuento aplicado, y el total general")
public record AplicarDescuentoResponse(
        List<VentaConDescuento> ventas,
        @Schema(example = "46350.0") double totalConDescuento) {
}
