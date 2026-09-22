package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Envoltorio estándar de respuesta del TP: toda respuesta (éxito o error) tiene esta forma.
 * Se nombra igual que la anotación de Swagger {@code @ApiResponse}; donde ambas conviven en el
 * mismo archivo, la anotación de Swagger se referencia por su nombre completo (FQN) para evitar
 * el choque de nombres en vez de renombrar esta clase.
 */
@Schema(description = "Envoltorio estándar de respuesta: código HTTP, mensaje y datos")
public record ApiResponse<T>(
        @Schema(example = "200") int status,
        @Schema(example = "Operación realizada con éxito") String message,
        T data) {

    public static <T> ApiResponse<T> of(int status, String message, T data) {
        return new ApiResponse<>(status, message, data);
    }
}
