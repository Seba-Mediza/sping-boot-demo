package com.aydsii.calculadora.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta propia del conversor de divisas: solo los datos relevantes para el
 * cliente, ya procesados a partir de la respuesta del servicio externo.
 */
@Schema(description = "Resultado de una conversión de divisas")
public record ConversionDivisa(
        @Schema(description = "Monto que se pidió convertir", example = "100") double montoOriginal,
        @Schema(description = "Código de la moneda de origen", example = "USD") String monedaOrigen,
        @Schema(description = "Código de la moneda de destino", example = "ARS") String monedaDestino,
        @Schema(description = "Tasa de cambio aplicada", example = "1234.56") double tasaCambio,
        @Schema(description = "Monto ya convertido", example = "123456.0") double montoConvertido,
        @Schema(description = "Fecha de la tasa de cambio", example = "2026-09-02") LocalDate fecha) {
}
