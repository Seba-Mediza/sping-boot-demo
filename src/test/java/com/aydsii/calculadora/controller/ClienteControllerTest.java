package com.aydsii.calculadora.controller;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.aydsii.calculadora.exception.EmailYaRegistradoException;
import com.aydsii.calculadora.model.Cliente;
import com.aydsii.calculadora.model.ClienteDTO;
import com.aydsii.calculadora.service.ClienteService;

@WebMvcTest(controllers = ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    private static final String BODY_VALIDO = """
            {"nombre":"Juan","apellido":"Pérez","email":"juan@mail.com","telefono":"1122334455"}
            """;

    @Test
    void crearValidadoDevuelve201YElClienteCreado() throws Exception {
        given(clienteService.crear(any(ClienteDTO.class)))
                .willReturn(new Cliente(1L, "Juan", "Pérez", "juan@mail.com", "1122334455"));

        mockMvc.perform(post("/api/clientes/validado").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Cliente creado"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("juan@mail.com"));
    }

    @Test
    void crearValidadoSinTelefonoEsValidoPorSerOpcional() throws Exception {
        String bodySinTelefono = """
                {"nombre":"Juan","apellido":"Pérez","email":"juan@mail.com"}
                """;
        given(clienteService.crear(any(ClienteDTO.class)))
                .willReturn(new Cliente(1L, "Juan", "Pérez", "juan@mail.com", null));

        mockMvc.perform(post("/api/clientes/validado").contentType(MediaType.APPLICATION_JSON).content(bodySinTelefono))
                .andExpect(status().isCreated());
    }

    @Test
    void crearValidadoConEmailYaRegistradoDevuelve400ConElFormatoPedido() throws Exception {
        given(clienteService.crear(any(ClienteDTO.class))).willThrow(new EmailYaRegistradoException());

        mockMvc.perform(post("/api/clientes/validado").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El email ya está registrado"))
                .andExpect(jsonPath("$.data").value(nullValue()));
    }

    @Test
    void crearValidadoConVariosCamposInvalidosDevuelve400ConTodosLosErrores() throws Exception {
        String bodyInvalido = """
                {"nombre":"A","apellido":"Pérez","email":"no-es-un-email","telefono":"abc123"}
                """;

        mockMvc.perform(post("/api/clientes/validado").contentType(MediaType.APPLICATION_JSON).content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.data.nombre").value("debe tener al menos 2 caracteres"))
                .andExpect(jsonPath("$.data.email").value("debe ser un email válido"))
                .andExpect(jsonPath("$.data.telefono").value("solo debe contener dígitos"))
                .andExpect(jsonPath("$.data.apellido").doesNotExist());

        verifyNoInteractions(clienteService);
    }

    @Test
    void crearValidadoConNombreVacioAcumulaLosDosMensajesDeEseCampo() throws Exception {
        String bodyInvalido = """
                {"nombre":"","apellido":"Pérez","email":"juan@mail.com","telefono":null}
                """;

        mockMvc.perform(post("/api/clientes/validado").contentType(MediaType.APPLICATION_JSON).content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.nombre").exists());

        verifyNoInteractions(clienteService);
    }
}
