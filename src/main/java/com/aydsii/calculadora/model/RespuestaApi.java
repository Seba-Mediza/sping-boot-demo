package com.aydsii.calculadora.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Envoltorio estándar de respuesta para los endpoints de Clientes: código HTTP,
 * mensaje legible y los datos (o null si no aplica, por ejemplo en un error).
 */
@Schema(description = "Envoltorio estándar de respuesta")
public record RespuestaApi<T>(
        @Schema(example = "201") int status,
        @Schema(example = "Cliente creado") String message,
        T data) {
}
