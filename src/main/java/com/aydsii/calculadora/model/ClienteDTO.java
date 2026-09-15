package com.aydsii.calculadora.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos de alta de un cliente")
public class ClienteDTO {

    @NotBlank(message = "no debe estar vacío")
    @Size(min = 2, message = "debe tener al menos 2 caracteres")
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @NotBlank(message = "no debe estar vacío")
    @Size(min = 2, message = "debe tener al menos 2 caracteres")
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @NotBlank(message = "no debe estar vacío")
    @Email(message = "debe ser un email válido")
    @Schema(description = "Email del cliente; no puede estar ya registrado", example = "juan.perez@mail.com")
    private String email;

    // Al ser opcional, @Pattern no se dispara si el valor es null: solo valida cuando se informa.
    @Pattern(regexp = "\\d+", message = "solo debe contener dígitos")
    @Schema(description = "Teléfono del cliente (opcional, solo dígitos)", example = "1122334455")
    private String telefono;
}
