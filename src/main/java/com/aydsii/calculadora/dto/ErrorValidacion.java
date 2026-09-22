package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Un error de validación sobre un elemento de una lista recibida")
public record ErrorValidacion(
        @Schema(description = "Posición (0-based) del elemento en la lista recibida", example = "2") int posicion,
        @Schema(description = "Campo inválido dentro de ese elemento", example = "cantidad") String campo,
        @Schema(description = "Motivo por el que no es válido", example = "la cantidad debe ser un entero positivo") String motivo) {
}
