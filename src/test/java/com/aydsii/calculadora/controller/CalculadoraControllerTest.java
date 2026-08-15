package com.aydsii.calculadora.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aydsii.calculadora.exception.GlobalExceptionHandler;
import com.aydsii.calculadora.service.CalculadoraService;

@WebMvcTest(controllers = CalculadoraController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class CalculadoraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculadoraService calculadoraService;

    @Test
    void sumarDevuelve200YElResultadoDelServicio() throws Exception {
        given(calculadoraService.sumar(2, 3)).willReturn(5.0);

        mockMvc.perform(get("/api/calculadora/sumar").param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operacion").value("suma"))
                .andExpect(jsonPath("$.resultado").value(5.0));
    }

    @Test
    void restarDevuelve200YElResultadoDelServicio() throws Exception {
        given(calculadoraService.restar(5, 3)).willReturn(2.0);

        mockMvc.perform(get("/api/calculadora/restar").param("a", "5").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operacion").value("resta"))
                .andExpect(jsonPath("$.resultado").value(2.0));
    }

    @Test
    void multiplicarDevuelve200YElResultadoDelServicio() throws Exception {
        given(calculadoraService.multiplicar(4, 3)).willReturn(12.0);

        mockMvc.perform(get("/api/calculadora/multiplicar").param("a", "4").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operacion").value("multiplicacion"))
                .andExpect(jsonPath("$.resultado").value(12.0));
    }

    @Test
    void dividirDevuelve200YElResultadoDelServicio() throws Exception {
        given(calculadoraService.dividir(6, 3)).willReturn(2.0);

        mockMvc.perform(get("/api/calculadora/dividir").param("a", "6").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operacion").value("division"))
                .andExpect(jsonPath("$.resultado").value(2.0));
    }

    @Test
    void dividirPorCeroDevuelve400() throws Exception {
        given(calculadoraService.dividir(6, 0)).willThrow(new ArithmeticException("No se puede dividir por cero"));

        mockMvc.perform(get("/api/calculadora/dividir").param("a", "6").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No se puede dividir por cero"));
    }
}
