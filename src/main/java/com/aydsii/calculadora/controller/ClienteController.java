package com.aydsii.calculadora.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.exception.EmailYaRegistradoException;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.ClienteDTO;
import com.aydsii.calculadora.model.RespuestaApi;
import com.aydsii.calculadora.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Alta de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/validado")
    @Operation(summary = "Alta de cliente con validación",
            description = "Valida los datos con Bean Validation y, antes de insertar, comprueba en la "
                    + "\"base de datos\" que el email no esté ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400",
                    description = "Datos inválidos (ver detalle por campo en 'data') o email ya registrado")
    })
    public ResponseEntity<RespuestaApi<Cliente>> crearValidado(@Valid @RequestBody ClienteDTO cliente) {
        Cliente creado = clienteService.crear(cliente);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespuestaApi<>(HttpStatus.CREATED.value(), "Cliente creado", creado));
    }

    // El email duplicado es una regla de negocio (se consulta la base de datos antes del INSERT), no de Bean Validation.
    @ExceptionHandler(EmailYaRegistradoException.class)
    public ResponseEntity<RespuestaApi<Object>> handleEmailDuplicado(EmailYaRegistradoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaApi<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    // Errores de Bean Validation del ClienteDTO (@NotBlank, @Size, @Email, @Pattern): se agrupan
    // todos los campos inválidos dentro de "data", campo -> motivo.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaApi<Map<String, String>>> handleValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.merge(error.getField(), error.getDefaultMessage(),
                    (mensajeActual, nuevoMensaje) -> mensajeActual + "; " + nuevoMensaje);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaApi<>(HttpStatus.BAD_REQUEST.value(), "Error de validación", errores));
    }
}
