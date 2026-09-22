package com.aydsii.calculadora.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aydsii.calculadora.exception.ServicioExternoException;
import com.aydsii.calculadora.exception.TpSpringExceptionHandler;
import com.aydsii.calculadora.model.ConversionDivisa;
import com.aydsii.calculadora.service.DivisaService;

@WebMvcTest(controllers = DivisaController.class)
@org.springframework.context.annotation.Import(TpSpringExceptionHandler.class)
class DivisaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DivisaService divisaService;

    @Test
    void convertirDevuelve200ConElEnvelopeEstandar() throws Exception {
        given(divisaService.convertir(100.0, "USD", "EUR"))
                .willReturn(new ConversionDivisa(100, "USD", "EUR", 0.87, 87.0, LocalDate.of(2026, 9, 22)));

        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "100")
                        .param("origen", "USD")
                        .param("destino", "EUR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Conversión realizada correctamente"))
                .andExpect(jsonPath("$.data.montoOriginal").value(100.0))
                .andExpect(jsonPath("$.data.monedaOrigen").value("USD"))
                .andExpect(jsonPath("$.data.tasaCambio").value(0.87))
                .andExpect(jsonPath("$.data.montoConvertido").value(87.0))
                .andExpect(jsonPath("$.data.fecha").value("2026-09-22"));
    }

    @Test
    void montoNoPositivoDevuelve400YNoLlamaAlServicio() throws Exception {
        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "0")
                        .param("origen", "USD")
                        .param("destino", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("el monto debe ser mayor que 0"))
                .andExpect(jsonPath("$.data").value(nullValue()));

        verifyNoInteractions(divisaService);
    }

    @Test
    void codigoDeMonedaMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "100")
                        .param("origen", "US")
                        .param("destino", "EUR"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("el código de origen debe ser un código de moneda de 3 letras"));

        verifyNoInteractions(divisaService);
    }

    @Test
    void problemaConElServicioExternoDevuelve502() throws Exception {
        given(divisaService.convertir(anyDouble(), any(), any()))
                .willThrow(new ServicioExternoException("No se pudo contactar al servicio externo"));

        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "100")
                        .param("origen", "USD")
                        .param("destino", "ARS"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message").value("No se pudo contactar al servicio externo"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }
}
