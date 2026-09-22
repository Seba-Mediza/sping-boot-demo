package com.aydsii.calculadora.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aydsii.calculadora.model.EstadoPedido;
import com.aydsii.calculadora.model.PedidoResumen;
import com.aydsii.calculadora.model.ProductoResumen;
import com.aydsii.calculadora.service.PedidoService;

@WebMvcTest(controllers = PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    void buscarSinParametrosDevuelveTodosLosPedidosEnvueltosEnRespuestaApi() throws Exception {
        PedidoResumen resumen = new PedidoResumen(12L, "Ana Garcia", LocalDate.of(2026, 8, 15),
                EstadoPedido.ENTREGADO, 9000.0,
                List.of(new ProductoResumen("Mouse inalambrico", "Perifericos", 2, 9000.0)));
        given(pedidoService.buscar(null, null, null, null, null)).willReturn(List.of(resumen));

        mockMvc.perform(get("/api/pedidos/buscar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Consulta realizada correctamente"))
                .andExpect(jsonPath("$.data[0].pedidoId").value(12))
                .andExpect(jsonPath("$.data[0].cliente").value("Ana Garcia"))
                .andExpect(jsonPath("$.data[0].productos[0].nombre").value("Mouse inalambrico"));
    }

    @Test
    void buscarPasaTodosLosFiltrosAlService() throws Exception {
        given(pedidoService.buscar(5L, "Perifericos", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31),
                EstadoPedido.ENTREGADO)).willReturn(List.of());

        mockMvc.perform(get("/api/pedidos/buscar")
                        .param("clienteId", "5")
                        .param("categoria", "Perifericos")
                        .param("fechaDesde", "2026-01-01")
                        .param("fechaHasta", "2026-12-31")
                        .param("estado", "ENTREGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void buscarSinCoincidenciasDevuelve200ConColeccionVacia() throws Exception {
        given(pedidoService.buscar(null, null, null, null, null)).willReturn(List.of());

        mockMvc.perform(get("/api/pedidos/buscar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Consulta realizada correctamente"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void buscarConEstadoInvalidoDevuelve400() throws Exception {
        mockMvc.perform(get("/api/pedidos/buscar").param("estado", "NO_EXISTE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("El estado debe ser uno de: PENDIENTE, ENVIADO, ENTREGADO, CANCELADO"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void buscarConFechaInvalidaDevuelve400() throws Exception {
        mockMvc.perform(get("/api/pedidos/buscar").param("fechaDesde", "15-08-2026"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("La fecha debe tener el formato yyyy-MM-dd"));
    }

    @Test
    void buscarConClienteIdInvalidoDevuelve400() throws Exception {
        mockMvc.perform(get("/api/pedidos/buscar").param("clienteId", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El clienteId debe ser un número"));
    }
}
