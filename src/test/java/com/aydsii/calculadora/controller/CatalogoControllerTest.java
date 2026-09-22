package com.aydsii.calculadora.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aydsii.calculadora.exception.ProductoNoEncontradoException;
import com.aydsii.calculadora.exception.StockInvalidoException;
import com.aydsii.calculadora.exception.TpSpringExceptionHandler;
import com.aydsii.calculadora.model.ProductoCatalogo;
import com.aydsii.calculadora.service.CatalogoService;

@WebMvcTest(controllers = CatalogoController.class)
@org.springframework.context.annotation.Import(TpSpringExceptionHandler.class)
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogoService catalogoService;

    @Test
    void listarDevuelve200ConElEnvelopeEstandar() throws Exception {
        given(catalogoService.listarTodos())
                .willReturn(List.of(new ProductoCatalogo(1L, "Mouse", "Perifericos", 4500.0, 15)));

        mockMvc.perform(get("/api/catalogo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].nombre").value("Mouse"));
    }

    @Test
    void buscarPasaLosFiltrosAlService() throws Exception {
        given(catalogoService.buscar("Perifericos", 1000.0, 20000.0)).willReturn(List.of());

        mockMvc.perform(get("/api/catalogo/buscar")
                        .param("categoria", "Perifericos")
                        .param("precioMin", "1000")
                        .param("precioMax", "20000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void ordenarUsaAscPorDefectoCuandoNoSeInformaOrden() throws Exception {
        given(catalogoService.ordenar("precio", "asc")).willReturn(List.of());

        mockMvc.perform(get("/api/catalogo/ordenar").param("criterio", "precio"))
                .andExpect(status().isOk());
    }

    @Test
    void ordenarConCriterioInvalidoDevuelve400() throws Exception {
        mockMvc.perform(get("/api/catalogo/ordenar").param("criterio", "color"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("criterio inválido: debe ser 'precio' o 'nombre'"));
    }

    @Test
    void agregarDevuelve201ConElProductoCreado() throws Exception {
        given(catalogoService.agregar(any()))
                .willReturn(new ProductoCatalogo(9L, "Teclado", "Perifericos", 25000.0, 10));

        mockMvc.perform(post("/api/catalogo").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","categoria":"Perifericos","precio":25000,"stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.id").value(9));
    }

    @Test
    void agregarConDatosInvalidosDevuelve400ConLosCamposInvalidos() throws Exception {
        mockMvc.perform(post("/api/catalogo").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"","categoria":"X","precio":-1,"stock":-1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.data.nombre").exists())
                .andExpect(jsonPath("$.data.precio").exists())
                .andExpect(jsonPath("$.data.stock").exists());
    }

    @Test
    void modificarStockDevuelve200ConElProductoActualizado() throws Exception {
        given(catalogoService.modificarStock(1L, 5))
                .willReturn(new ProductoCatalogo(1L, "Mouse", "Perifericos", 4500.0, 20));

        mockMvc.perform(put("/api/catalogo/1/stock").param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(20));
    }

    @Test
    void modificarStockQueDejariaNegativoDevuelve400() throws Exception {
        given(catalogoService.modificarStock(1L, -999))
                .willThrow(new StockInvalidoException("El stock no puede quedar por debajo de 0"));

        mockMvc.perform(put("/api/catalogo/1/stock").param("cantidad", "-999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void modificarStockDeProductoInexistenteDevuelve404() throws Exception {
        given(catalogoService.modificarStock(999L, 1)).willThrow(new ProductoNoEncontradoException(999L));

        mockMvc.perform(put("/api/catalogo/999/stock").param("cantidad", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarDevuelve200ConMensajeDeConfirmacion() throws Exception {
        mockMvc.perform(delete("/api/catalogo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Producto eliminado correctamente"));
    }

    @Test
    void eliminarProductoInexistenteDevuelve404() throws Exception {
        doThrow(new ProductoNoEncontradoException(999L)).when(catalogoService).eliminar(999L);

        mockMvc.perform(delete("/api/catalogo/999"))
                .andExpect(status().isNotFound());
    }
}
