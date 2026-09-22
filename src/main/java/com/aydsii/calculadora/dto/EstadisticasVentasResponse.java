package com.aydsii.calculadora.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estadísticas calculadas sobre un lote de ventas")
public record EstadisticasVentasResponse(
        @Schema(example = "51500.0") double totalFacturado,
        @Schema(example = "4") int cantidadVentas,
        @Schema(example = "12875.0") double ticketPromedio,
        VentaConImporte ventaMayor,
        VentaConImporte ventaMenor,
        @Schema(example = "Mouse inalambrico") String productoMasVendido) {
}
