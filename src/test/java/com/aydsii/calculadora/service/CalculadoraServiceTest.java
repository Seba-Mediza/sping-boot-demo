package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CalculadoraServiceTest {

    private final CalculadoraService calculadoraService = new CalculadoraService();

    @Test
    void sumarDevuelveLaSumaDeAmbosNumeros() {
        assertThat(calculadoraService.sumar(2, 3)).isEqualTo(5);
    }

    @Test
    void restarDevuelveLaRestaDeAmbosNumeros() {
        assertThat(calculadoraService.restar(5, 3)).isEqualTo(2);
    }

    @Test
    void multiplicarDevuelveElProductoDeAmbosNumeros() {
        assertThat(calculadoraService.multiplicar(4, 3)).isEqualTo(12);
    }

    @Test
    void dividirDevuelveElCocienteDeAmbosNumeros() {
        assertThat(calculadoraService.dividir(6, 3)).isEqualTo(2);
    }

    @Test
    void dividirPorCeroLanzaArithmeticException() {
        assertThatThrownBy(() -> calculadoraService.dividir(6, 0))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("No se puede dividir por cero");
    }
}
