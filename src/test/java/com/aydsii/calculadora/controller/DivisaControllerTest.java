package com.aydsii.calculadora.controller;

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

import com.aydsii.calculadora.exception.GlobalExceptionHandler;
import com.aydsii.calculadora.exception.ServicioExternoException;
import com.aydsii.calculadora.model.ConversionDivisa;
import com.aydsii.calculadora.service.DivisaService;

@WebMvcTest(controllers = DivisaController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class DivisaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DivisaService divisaService;

    @Test
    void convertirDevuelve200YLaRespuestaProcesada() throws Exception {
        given(divisaService.convertir(100.0, "USD", "ARS"))
                .willReturn(new ConversionDivisa(100, "USD", "ARS", 1234.56, 123456.0, LocalDate.of(2026, 9, 2)));

        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "100")
                        .param("origen", "USD")
                        .param("destino", "ARS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montoOriginal").value(100.0))
                .andExpect(jsonPath("$.monedaOrigen").value("USD"))
                .andExpect(jsonPath("$.tasaCambio").value(1234.56))
                .andExpect(jsonPath("$.montoConvertido").value(123456.0))
                .andExpect(jsonPath("$.fecha").value("2026-09-02"));
    }

    @Test
    void montoNoPositivoDevuelve400YNoLlamaAlServicio() throws Exception {
        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "0")
                        .param("origen", "USD")
                        .param("destino", "ARS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El monto debe ser mayor que 0"));

        verifyNoInteractions(divisaService);
    }

    @Test
    void codigoDeMonedaMalFormadoDevuelve400() throws Exception {
        mockMvc.perform(get("/api/divisas/convertir")
                        .param("monto", "100")
                        .param("origen", "US")
                        .param("destino", "ARS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El código de origen debe ser de 3 letras"));

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
                .andExpect(jsonPath("$.error").value("No se pudo contactar al servicio externo"));
    }
}
