package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Datos para dar de alta un producto en el catálogo")
public record ProductoDTO(
        @NotBlank(message = "el nombre no puede estar vacío")
        @Schema(example = "Teclado") String nombre,

        @Schema(example = "Perifericos") String categoria,

        @Positive(message = "el precio debe ser mayor que 0")
        @Schema(example = "25000") double precio,

        @PositiveOrZero(message = "el stock debe ser mayor o igual que 0")
        @Schema(example = "10") int stock) {
}
