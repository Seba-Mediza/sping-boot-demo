package com.aydsii.calculadora.model;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de un pedido: datos generales, cliente y productos")
public record PedidoResumen(
        @Schema(example = "12") Long pedidoId,
        @Schema(example = "Ana Garcia") String cliente,
        @Schema(example = "2026-08-15") LocalDate fecha,
        @Schema(example = "ENTREGADO") EstadoPedido estado,
        @Schema(example = "25400.0") double totalPedido,
        List<ProductoResumen> productos) {
}
