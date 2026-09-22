package com.aydsii.calculadora.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.PedidoResumen;
import com.aydsii.calculadora.model.RespuestaApi;
import com.aydsii.calculadora.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Historial de pedidos con filtros opcionales")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca pedidos combinando filtros opcionales (AND)",
            description = "Todos los parámetros son opcionales y, si se informan varios, se combinan con AND. "
                    + "Sin ningún parámetro devuelve todos los pedidos con sus detalles. Si ningún pedido cumple "
                    + "los filtros no es un error: responde 200 con data: [].")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada correctamente (puede ser una colección vacía)"),
            @ApiResponse(responseCode = "400", description = "Un parámetro tiene un formato inválido")
    })
    public ResponseEntity<RespuestaApi<List<PedidoResumen>>> buscar(
            @Parameter(description = "Id del cliente") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "El pedido debe incluir al menos un producto de esta categoría",
                    example = "Perifericos") @RequestParam(required = false) String categoria,
            @Parameter(description = "Fecha inicial del período", example = "2026-08-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @Parameter(description = "Fecha final del período", example = "2026-08-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @Parameter(description = "Estado del pedido", example = "ENTREGADO")
            @RequestParam(required = false) EstadoPedido estado) {
        List<PedidoResumen> pedidos = pedidoService.buscar(clienteId, categoria, fechaDesde, fechaHasta, estado);
        return ResponseEntity
                .ok(new RespuestaApi<>(HttpStatus.OK.value(), "Consulta realizada correctamente", pedidos));
    }

    // Se dispara cuando un @RequestParam no puede convertirse al tipo esperado: estado (enum),
    // fechaDesde/fechaHasta (LocalDate) o clienteId (Long).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespuestaApi<Object>> handleParametroInvalido(MethodArgumentTypeMismatchException ex) {
        String mensaje = switch (ex.getName()) {
            case "estado" -> "El estado debe ser uno de: PENDIENTE, ENVIADO, ENTREGADO, CANCELADO";
            case "fechaDesde", "fechaHasta" -> "La fecha debe tener el formato yyyy-MM-dd";
            case "clienteId" -> "El clienteId debe ser un número";
            default -> "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        };
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new RespuestaApi<>(HttpStatus.BAD_REQUEST.value(), mensaje, null));
    }
}
