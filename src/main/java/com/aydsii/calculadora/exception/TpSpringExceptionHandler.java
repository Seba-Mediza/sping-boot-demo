package com.aydsii.calculadora.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.aydsii.calculadora.controller.CatalogoController;
import com.aydsii.calculadora.controller.DivisaController;
import com.aydsii.calculadora.controller.VentaController;
import com.aydsii.calculadora.dto.ApiResponse;

/**
 * Manejo de errores centralizado de los módulos del TP Spring (Ventas, Catálogo, Divisas):
 * toda respuesta, incluidos los errores, respeta el formato {status, message, data}.
 *
 * Se restringe a estos tres controllers (assignableTypes) para no alterar el formato de error
 * que ya usan los ejercicios anteriores (Calculadora, Libros, Clientes, Pedidos), que sigue a
 * cargo de GlobalExceptionHandler. @Order(1) hace explícito que, para esos tres controllers,
 * este advice se evalúa antes que GlobalExceptionHandler (que no tiene @Order y por lo tanto
 * queda en una precedencia menor) cuando ambos podrían aplicar al mismo tipo de excepción.
 */
@RestControllerAdvice(assignableTypes = { VentaController.class, CatalogoController.class, DivisaController.class })
@Order(1)
public class TpSpringExceptionHandler {

    // Errores de un solo objeto en el body (por ejemplo ProductoDTO): se informa campo -> motivo.
    // El caso "lista de ventas con elementos inválidos" no pasa por acá: se valida a mano en
    // VentaService (ver ValidacionListaException) porque necesita informar además la posición
    // de cada elemento, algo que MethodArgumentNotValidException no expone de forma confiable
    // para una List<T> validada por cascada.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidacion(MethodArgumentNotValidException ex) {
        List<FieldError> erroresDeCampo = ex.getBindingResult().getFieldErrors();

        if (!erroresDeCampo.isEmpty()) {
            Map<String, String> camposInvalidos = new HashMap<>();
            for (FieldError error : erroresDeCampo) {
                camposInvalidos.merge(error.getField(), error.getDefaultMessage(),
                        (actual, nuevo) -> actual + "; " + nuevo);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), "Error de validación", camposInvalidos));
        }

        // Sin FieldError puntual: por ejemplo @NotEmpty sobre la lista completa (viene como
        // error global, no asociado a un campo).
        String mensaje = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), mensaje, null));
    }

    // Uno o más elementos de una lista (ej. VentaDTO) no cumplen las validaciones: se informa
    // la posición, el campo y el motivo de cada error, ya armados por el Service.
    @ExceptionHandler(ValidacionListaException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidacionLista(ValidacionListaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getErrores()));
    }

    // Se dispara cuando falla la validación de un @RequestParam / @PathVariable
    // (@Positive, @Pattern, @DecimalMin, @DecimalMax, ...).
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleParametrosInvalidos(HandlerMethodValidationException ex) {
        String mensaje = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), mensaje, null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ApiResponse<Object>> handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(StockInvalidoException.class)
    public ResponseEntity<ApiResponse<Object>> handleStockInvalido(StockInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    // Problemas al comunicarse con el servicio externo (Frankfurter) -> 502 Bad Gateway.
    @ExceptionHandler(ServicioExternoException.class)
    public ResponseEntity<ApiResponse<Object>> handleServicioExterno(ServicioExternoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.of(HttpStatus.BAD_GATEWAY.value(), ex.getMessage(), null));
    }

    // Cualquier error no contemplado explícitamente -> 500, sin filtrar detalles internos al cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleErrorNoControlado(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocurrió un error inesperado", null));
    }
}
