package com.aydsii.calculadora.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Data (Lombok): genera getters, setters, toString, equals y hashCode en tiempo de compilación.
@Data
// @NoArgsConstructor (Lombok): genera el constructor vacío, necesario para que Jackson deserialice el JSON.
@NoArgsConstructor
// @AllArgsConstructor (Lombok): genera un constructor con todos los campos (el que usamos en Storage y los tests).
@AllArgsConstructor
// @Schema (Swagger/OpenAPI): describe este modelo en la documentación de la API.
@Schema(description = "Representa un libro del catálogo")
public class Libro {

    // @NotBlank (Bean Validation): el valor no puede ser null ni estar vacío / solo espacios.
    @NotBlank(message = "El título es obligatorio")
    @Schema(description = "Título del libro", example = "Cien años de soledad")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Schema(description = "Autor del libro", example = "Gabriel García Márquez")
    private String autor;

    @NotBlank(message = "El ISBN es obligatorio")
    @Schema(description = "ISBN, identificador único del libro", example = "978-0307474728")
    private String isbn;

    @NotBlank(message = "El género es obligatorio")
    @Schema(description = "Género literario", example = "Realismo mágico")
    private String genero;

    // @Min (Bean Validation): la cantidad no puede ser negativa.
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Schema(description = "Cantidad de ejemplares disponibles", example = "5")
    private int cantidadDisponibles;
}
