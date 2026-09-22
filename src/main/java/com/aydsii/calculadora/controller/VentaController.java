package com.aydsii.calculadora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.dto.AplicarDescuentoResponse;
import com.aydsii.calculadora.dto.ApiResponse;
import com.aydsii.calculadora.dto.EstadisticasVentasResponse;
import com.aydsii.calculadora.dto.VentaDTO;
import com.aydsii.calculadora.service.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

// @RestController: controlador REST del módulo de procesamiento de ventas (TP Spring, Ejercicio 1).
@RestController
@RequestMapping("/api/ventas")
@Tag(name = "TP Spring - Ventas", description = "Procesamiento en memoria de lotes de ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/estadisticas")
    @Operation(summary = "Calcula estadísticas de un lote de ventas",
            description = "Recibe una lista de ventas y devuelve total facturado, cantidad, ticket promedio, "
                    + "venta mayor y menor, y el producto más vendido.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Estadísticas calculadas correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Lista vacía o algún elemento no cumple las validaciones")
    })
    public ResponseEntity<ApiResponse<EstadisticasVentasResponse>> estadisticas(@RequestBody List<VentaDTO> ventas) {
        EstadisticasVentasResponse resultado = ventaService.calcularEstadisticas(ventas);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), "Operación realizada con éxito", resultado));
    }

    @PostMapping("/aplicar-descuento")
    @Operation(summary = "Aplica un descuento a un lote de ventas",
            description = "Recibe una lista de ventas y un porcentaje de descuento (0 a 100), y devuelve cada "
                    + "venta con su montoConDescuento y el totalConDescuento general.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Descuento aplicado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Lista vacía, algún elemento inválido, o porcentaje fuera de [0, 100]")
    })
    public ResponseEntity<ApiResponse<AplicarDescuentoResponse>> aplicarDescuento(
            @RequestBody List<VentaDTO> ventas,
            @Parameter(description = "Porcentaje de descuento a aplicar, entre 0 y 100", example = "10")
            @RequestParam
            @DecimalMin(value = "0", message = "el porcentaje no es válido: debe estar entre 0 y 100")
            @DecimalMax(value = "100", message = "el porcentaje no es válido: debe estar entre 0 y 100")
            double porcentaje) {
        AplicarDescuentoResponse resultado = ventaService.aplicarDescuento(ventas, porcentaje);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), "Operación realizada con éxito", resultado));
    }
}
