package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "Una venta recibida en el lote a procesar")
public record VentaDTO(
        @NotBlank(message = "el producto no puede estar vacío")
        @Schema(example = "Mouse inalambrico") String producto,

        @Positive(message = "la cantidad debe ser un entero positivo")
        @Schema(example = "3") int cantidad,

        @Positive(message = "el precioUnitario debe ser mayor que 0")
        @Schema(example = "4500.0") double precioUnitario) {

    public double importe() {
        return cantidad * precioUnitario;
    }
}
