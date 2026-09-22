package com.aydsii.calculadora.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aydsii.calculadora.dto.AplicarDescuentoResponse;
import com.aydsii.calculadora.dto.ErrorValidacion;
import com.aydsii.calculadora.dto.EstadisticasVentasResponse;
import com.aydsii.calculadora.dto.VentaConDescuento;
import com.aydsii.calculadora.dto.VentaConImporte;
import com.aydsii.calculadora.exception.TpSpringExceptionHandler;
import com.aydsii.calculadora.exception.ValidacionListaException;
import com.aydsii.calculadora.service.VentaService;

@WebMvcTest(controllers = VentaController.class)
@org.springframework.context.annotation.Import(TpSpringExceptionHandler.class)
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VentaService ventaService;

    private static final String BODY = """
            [{"producto":"Mouse","cantidad":3,"precioUnitario":4500}]
            """;

    @Test
    void estadisticasDevuelve200ConElEnvelopeEstandar() throws Exception {
        EstadisticasVentasResponse resumen = new EstadisticasVentasResponse(13500.0, 1, 13500.0,
                new VentaConImporte("Mouse", 3, 4500.0, 13500.0),
                new VentaConImporte("Mouse", 3, 4500.0, 13500.0),
                "Mouse");
        given(ventaService.calcularEstadisticas(any())).willReturn(resumen);

        mockMvc.perform(post("/api/ventas/estadisticas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Operación realizada con éxito"))
                .andExpect(jsonPath("$.data.totalFacturado").value(13500.0))
                .andExpect(jsonPath("$.data.productoMasVendido").value("Mouse"));
    }

    @Test
    void estadisticasConListaVaciaDevuelve400ConMensajeClaro() throws Exception {
        given(ventaService.calcularEstadisticas(any()))
                .willThrow(new ValidacionListaException("la lista de ventas no puede estar vacía"));

        mockMvc.perform(post("/api/ventas/estadisticas").contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("la lista de ventas no puede estar vacía"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void estadisticasConElementoInvalidoDevuelve400ConPosicionCampoYMotivo() throws Exception {
        given(ventaService.calcularEstadisticas(any())).willThrow(new ValidacionListaException(
                List.of(new ErrorValidacion(1, "cantidad", "la cantidad debe ser un entero positivo"))));

        mockMvc.perform(post("/api/ventas/estadisticas").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.data[0].posicion").value(1))
                .andExpect(jsonPath("$.data[0].campo").value("cantidad"))
                .andExpect(jsonPath("$.data[0].motivo").value("la cantidad debe ser un entero positivo"));
    }

    @Test
    void aplicarDescuentoDevuelve200ConElTotal() throws Exception {
        AplicarDescuentoResponse resultado = new AplicarDescuentoResponse(
                List.of(new VentaConDescuento("Mouse", 3, 4500.0, 12150.0)), 12150.0);
        given(ventaService.aplicarDescuento(any(), eq(10.0))).willReturn(resultado);

        mockMvc.perform(post("/api/ventas/aplicar-descuento?porcentaje=10")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalConDescuento").value(12150.0));
    }

    @Test
    void aplicarDescuentoConPorcentajeFueraDeRangoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/ventas/aplicar-descuento?porcentaje=150")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("el porcentaje no es válido: debe estar entre 0 y 100"));
    }

    @Test
    void aplicarDescuentoConPorcentajeNegativoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/ventas/aplicar-descuento?porcentaje=-1")
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest());
    }
}
