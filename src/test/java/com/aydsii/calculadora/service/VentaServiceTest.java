package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.aydsii.calculadora.dto.AplicarDescuentoResponse;
import com.aydsii.calculadora.dto.EstadisticasVentasResponse;
import com.aydsii.calculadora.dto.VentaDTO;
import com.aydsii.calculadora.exception.ValidacionListaException;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

class VentaServiceTest {

    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        ventaService = new VentaService(validator);
    }

    @Test
    void calcularEstadisticasSumaElImporteDeCadaVentaParaElTotalFacturado() {
        List<VentaDTO> ventas = List.of(
                new VentaDTO("Mouse", 3, 4500.0),
                new VentaDTO("Teclado", 1, 12000.0));

        EstadisticasVentasResponse resultado = ventaService.calcularEstadisticas(ventas);

        assertThat(resultado.totalFacturado()).isEqualTo(25500.0);
        assertThat(resultado.cantidadVentas()).isEqualTo(2);
        assertThat(resultado.ticketPromedio()).isEqualTo(12750.0);
    }

    @Test
    void calcularEstadisticasIdentificaVentaMayorYMenorPorImporte() {
        List<VentaDTO> ventas = List.of(
                new VentaDTO("Mouse", 3, 4500.0), // importe 13500
                new VentaDTO("Teclado", 1, 12000.0)); // importe 12000

        EstadisticasVentasResponse resultado = ventaService.calcularEstadisticas(ventas);

        assertThat(resultado.ventaMayor().producto()).isEqualTo("Mouse");
        assertThat(resultado.ventaMayor().importe()).isEqualTo(13500.0);
        assertThat(resultado.ventaMenor().producto()).isEqualTo("Teclado");
        assertThat(resultado.ventaMenor().importe()).isEqualTo(12000.0);
    }

    @Test
    void calcularEstadisticasAcumulaCantidadDelMismoProductoParaElMasVendido() {
        List<VentaDTO> ventas = List.of(
                new VentaDTO("Mouse", 3, 4500.0),
                new VentaDTO("Teclado", 4, 100.0),
                new VentaDTO("Mouse", 5, 4500.0)); // Mouse acumula 8, Teclado 4

        EstadisticasVentasResponse resultado = ventaService.calcularEstadisticas(ventas);

        assertThat(resultado.productoMasVendido()).isEqualTo("Mouse");
    }

    @Test
    void calcularEstadisticasConListaVaciaLanzaValidacionListaExceptionConMensajeClaro() {
        assertThatThrownBy(() -> ventaService.calcularEstadisticas(List.of()))
                .isInstanceOf(ValidacionListaException.class)
                .hasMessage("la lista de ventas no puede estar vacía");
    }

    @Test
    void calcularEstadisticasConElementoInvalidoInformaPosicionCampoYMotivo() {
        List<VentaDTO> ventas = List.of(
                new VentaDTO("Mouse", 3, 4500.0),
                new VentaDTO("", -1, 4500.0));

        ValidacionListaException ex = org.junit.jupiter.api.Assertions.assertThrows(ValidacionListaException.class,
                () -> ventaService.calcularEstadisticas(ventas));

        assertThat(ex.getErrores()).hasSize(2);
        assertThat(ex.getErrores()).allMatch(error -> error.posicion() == 1);
        assertThat(ex.getErrores()).extracting("campo").containsExactlyInAnyOrder("producto", "cantidad");
    }

    @Test
    void aplicarDescuentoCalculaElMontoConDescuentoYElTotal() {
        List<VentaDTO> ventas = List.of(new VentaDTO("Mouse", 2, 100.0)); // importe 200

        AplicarDescuentoResponse resultado = ventaService.aplicarDescuento(ventas, 10);

        assertThat(resultado.ventas().get(0).montoConDescuento()).isEqualTo(180.0);
        assertThat(resultado.totalConDescuento()).isEqualTo(180.0);
    }

    @Test
    void aplicarDescuentoConCeroNoModificaElMonto() {
        List<VentaDTO> ventas = List.of(new VentaDTO("Mouse", 2, 100.0));

        AplicarDescuentoResponse resultado = ventaService.aplicarDescuento(ventas, 0);

        assertThat(resultado.totalConDescuento()).isEqualTo(200.0);
    }

    @Test
    void aplicarDescuentoConCienDejaElMontoEnCero() {
        List<VentaDTO> ventas = List.of(new VentaDTO("Mouse", 2, 100.0));

        AplicarDescuentoResponse resultado = ventaService.aplicarDescuento(ventas, 100);

        assertThat(resultado.totalConDescuento()).isEqualTo(0.0);
    }
}
