package com.aydsii.calculadora.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA persistida en MySQL, en la tabla "clientes" (Hibernate la crea
 * al levantar el proyecto gracias a spring.jpa.hibernate.ddl-auto=update).
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cliente persistido en la base de datos")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador generado por la base de datos", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Column(nullable = false)
    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @Column(nullable = false, unique = true)
    @Schema(description = "Email del cliente, único en la base de datos", example = "juan.perez@mail.com")
    private String email;

    @Schema(description = "Teléfono del cliente (opcional)", example = "1122334455")
    private String telefono;

    public Cliente(String nombre, String apellido, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
    }
}
