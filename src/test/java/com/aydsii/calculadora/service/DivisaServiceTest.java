package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.aydsii.calculadora.exception.ServicioExternoException;
import com.aydsii.calculadora.model.ConversionDivisa;

class DivisaServiceTest {

    private static final String FRANKFURTER_JSON = """
            {"amount":1.0,"base":"USD","date":"2026-09-22","rates":{"EUR":0.8724}}
            """;

    private MockRestServiceServer server;
    private DivisaService divisaService;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        divisaService = new DivisaService(builder.build());
    }

    @Test
    void convertirConsultaFrankfurterYArmaLaRespuestaPropia() {
        server.expect(requestTo("/latest?amount=1&from=USD&to=EUR"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(FRANKFURTER_JSON, MediaType.APPLICATION_JSON));

        ConversionDivisa resultado = divisaService.convertir(100, "usd", "eur");

        assertThat(resultado.montoOriginal()).isEqualTo(100);
        assertThat(resultado.monedaOrigen()).isEqualTo("USD");
        assertThat(resultado.monedaDestino()).isEqualTo("EUR");
        assertThat(resultado.tasaCambio()).isEqualTo(0.87);
        assertThat(resultado.montoConvertido()).isEqualTo(87.24);
        assertThat(resultado.fecha()).isEqualTo(LocalDate.of(2026, 9, 22));
        server.verify();
    }

    @Test
    void convertirLanza502CuandoFrankfurterDevuelveErrorHttp() {
        server.expect(requestTo("/latest?amount=1&from=USD&to=ARS")).andRespond(withServerError());

        assertThatThrownBy(() -> divisaService.convertir(100, "USD", "ARS"))
                .isInstanceOf(ServicioExternoException.class)
                .hasMessageContaining("error HTTP 500");
    }

    @Test
    void convertirLanza502CuandoLaMonedaNoExistePeroLaRespuestaEs200SinLaTasa() {
        server.expect(requestTo("/latest?amount=1&from=USD&to=XXX"))
                .andRespond(withSuccess("""
                        {"amount":1.0,"base":"USD","date":"2026-09-22","rates":{}}
                        """, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> divisaService.convertir(100, "USD", "XXX"))
                .isInstanceOf(ServicioExternoException.class)
                .hasMessageContaining("no devolvió");
    }
}
