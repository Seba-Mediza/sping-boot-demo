package com.aydsii.calculadora.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.aydsii.calculadora.dto.AplicarDescuentoResponse;
import com.aydsii.calculadora.dto.ErrorValidacion;
import com.aydsii.calculadora.dto.EstadisticasVentasResponse;
import com.aydsii.calculadora.dto.VentaConDescuento;
import com.aydsii.calculadora.dto.VentaConImporte;
import com.aydsii.calculadora.dto.VentaDTO;
import com.aydsii.calculadora.exception.ValidacionListaException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

// @Service: procesa lotes de ventas en memoria; no persiste nada.
@Service
public class VentaService {

    private final Validator validator;

    public VentaService(Validator validator) {
        this.validator = validator;
    }

    public EstadisticasVentasResponse calcularEstadisticas(List<VentaDTO> ventas) {
        validarElementos(ventas);

        double totalFacturado = ventas.stream().mapToDouble(VentaDTO::importe).sum();
        int cantidadVentas = ventas.size();
        double ticketPromedio = totalFacturado / cantidadVentas;

        VentaDTO ventaMayor = ventas.stream().max(Comparator.comparingDouble(VentaDTO::importe)).orElseThrow();
        VentaDTO ventaMenor = ventas.stream().min(Comparator.comparingDouble(VentaDTO::importe)).orElseThrow();

        String productoMasVendido = ventas.stream()
                .collect(Collectors.groupingBy(VentaDTO::producto, Collectors.summingInt(VentaDTO::cantidad)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();

        return new EstadisticasVentasResponse(
                redondear(totalFacturado),
                cantidadVentas,
                redondear(ticketPromedio),
                aVentaConImporte(ventaMayor),
                aVentaConImporte(ventaMenor),
                productoMasVendido);
    }

    public AplicarDescuentoResponse aplicarDescuento(List<VentaDTO> ventas, double porcentaje) {
        validarElementos(ventas);

        List<VentaConDescuento> conDescuento = ventas.stream()
                .map(venta -> aVentaConDescuento(venta, porcentaje))
                .toList();

        double totalConDescuento = conDescuento.stream().mapToDouble(VentaConDescuento::montoConDescuento).sum();

        return new AplicarDescuentoResponse(conDescuento, redondear(totalConDescuento));
    }

    private VentaConImporte aVentaConImporte(VentaDTO venta) {
        return new VentaConImporte(venta.producto(), venta.cantidad(), venta.precioUnitario(),
                redondear(venta.importe()));
    }

    private VentaConDescuento aVentaConDescuento(VentaDTO venta, double porcentaje) {
        double montoConDescuento = venta.importe() * (1 - porcentaje / 100.0);
        return new VentaConDescuento(venta.producto(), venta.cantidad(), venta.precioUnitario(),
                redondear(montoConDescuento));
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /**
     * Valida la lista completa a mano (sin @Valid en el controller): primero que no esté vacía,
     * y después cada VentaDTO con Bean Validation, juntando todos los errores encontrados con la
     * posición del elemento, el campo y el motivo (como pide el enunciado). Se hace así, en vez
     * de dejar que @Valid cascadee sobre la List, porque Spring no expone de forma confiable la
     * posición del elemento en ese caso (y @Valid sobre una List siempre cascada, sin importar
     * si el tipo se escribe como List&lt;VentaDTO&gt; o List&lt;@Valid VentaDTO&gt;).
     */
    private void validarElementos(List<VentaDTO> ventas) {
        if (ventas.isEmpty()) {
            throw new ValidacionListaException("la lista de ventas no puede estar vacía");
        }

        List<ErrorValidacion> errores = new ArrayList<>();
        for (int posicion = 0; posicion < ventas.size(); posicion++) {
            Set<ConstraintViolation<VentaDTO>> violaciones = validator.validate(ventas.get(posicion));
            int posicionFinal = posicion;
            violaciones.forEach(violacion -> errores.add(new ErrorValidacion(posicionFinal,
                    violacion.getPropertyPath().toString(), violacion.getMessage())));
        }
        if (!errores.isEmpty()) {
            throw new ValidacionListaException(errores);
        }
    }
}
