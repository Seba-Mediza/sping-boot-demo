package com.aydsii.calculadora.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
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

    private static final String POSTS_JSON = """
            [
              {"userId":1,"id":1,"title":"a","body":"b"},
              {"userId":1,"id":2,"title":"c","body":"d"}
            ]
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
    void convertirConsultaElServicioExternoYArmaLaRespuestaPropia() {
        server.expect(requestTo("/posts"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(POSTS_JSON, MediaType.APPLICATION_JSON));

        ConversionDivisa resultado = divisaService.convertir(100, "usd", "ars");

        assertThat(resultado.montoOriginal()).isEqualTo(100);
        assertThat(resultado.monedaOrigen()).isEqualTo("USD");
        assertThat(resultado.monedaDestino()).isEqualTo("ARS");
        assertThat(resultado.fecha()).isEqualTo(LocalDate.now());
        assertThat(resultado.tasaCambio()).isPositive();
        assertThat(resultado.montoConvertido()).isCloseTo(resultado.tasaCambio() * 100, within(1.0));
        server.verify();
    }

    @Test
    void convertirLanza502CuandoElServicioExternoDevuelveErrorHttp() {
        server.expect(requestTo("/posts")).andRespond(withServerError());

        assertThatThrownBy(() -> divisaService.convertir(100, "USD", "ARS"))
                .isInstanceOf(ServicioExternoException.class)
                .hasMessageContaining("error HTTP 500");
    }

    @Test
    void convertirLanza502CuandoElServicioExternoNoDevuelveInformacion() {
        server.expect(requestTo("/posts")).andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> divisaService.convertir(100, "USD", "ARS"))
                .isInstanceOf(ServicioExternoException.class)
                .hasMessageContaining("no devolvió información");
    }
}
