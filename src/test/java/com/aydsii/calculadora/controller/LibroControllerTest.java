package com.aydsii.calculadora.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import com.aydsii.calculadora.exception.GlobalExceptionHandler;
import com.aydsii.calculadora.exception.LibroNoEncontradoException;
import com.aydsii.calculadora.exception.LibroYaExisteException;
import com.aydsii.calculadora.model.Libro;
import com.aydsii.calculadora.service.LibroService;

@WebMvcTest(controllers = LibroController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class LibroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LibroService libroService;

    private static final String BODY = """
            {"titulo":"1984","autor":"George Orwell","isbn":"978-0451524935","genero":"Distopía","cantidadDisponibles":8}
            """;

    @Test
    void listarDevuelve200YLaListaDeLibros() throws Exception {
        given(libroService.buscar(null, null, null, null, null, false))
                .willReturn(List.of(new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 8)));

        mockMvc.perform(get("/api/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("1984"))
                .andExpect(jsonPath("$[0].isbn").value("978-0451524935"));
    }

    @Test
    void listarPasaLosFiltrosYElOrdenAlServicio() throws Exception {
        given(libroService.buscar("1984", null, "Distopía", true, "titulo", true))
                .willReturn(List.of(new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 8)));

        mockMvc.perform(get("/api/libros")
                        .param("titulo", "1984")
                        .param("genero", "Distopía")
                        .param("soloDisponibles", "true")
                        .param("ordenarPor", "titulo")
                        .param("orden", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("978-0451524935"));
    }

    @Test
    void listarConCampoDeOrdenInvalidoDevuelve400() throws Exception {
        given(libroService.buscar(null, null, null, null, "paginas", false))
                .willThrow(new IllegalArgumentException("No se puede ordenar por 'paginas'."));

        mockMvc.perform(get("/api/libros").param("ordenarPor", "paginas"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No se puede ordenar por 'paginas'."));
    }

    @Test
    void obtenerDevuelve200YElLibro() throws Exception {
        given(libroService.obtener("978-0451524935"))
                .willReturn(new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 8));

        mockMvc.perform(get("/api/libros/978-0451524935"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autor").value("George Orwell"));
    }

    @Test
    void obtenerLibroInexistenteDevuelve404() throws Exception {
        given(libroService.obtener("000")).willThrow(new LibroNoEncontradoException("000"));

        mockMvc.perform(get("/api/libros/000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe un libro con ISBN 000"));
    }

    @Test
    void crearDevuelve201YElLibroCreado() throws Exception {
        given(libroService.crear(any(Libro.class)))
                .willReturn(new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 8));

        mockMvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("1984"));
    }

    @Test
    void crearConTituloVacioDevuelve400YElErrorDelCampo() throws Exception {
        String bodyInvalido = """
                {"titulo":"  ","autor":"George Orwell","isbn":"978-0451524935","genero":"Distopía","cantidadDisponibles":8}
                """;

        mockMvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.titulo").value("El título es obligatorio"));

        verifyNoInteractions(libroService);
    }

    @Test
    void crearConCantidadNegativaDevuelve400() throws Exception {
        String bodyInvalido = """
                {"titulo":"1984","autor":"George Orwell","isbn":"978-0451524935","genero":"Distopía","cantidadDisponibles":-1}
                """;

        mockMvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content(bodyInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cantidadDisponibles").value("La cantidad disponible no puede ser negativa"));
    }

    @Test
    void crearLibroDuplicadoDevuelve409() throws Exception {
        given(libroService.crear(any(Libro.class))).willThrow(new LibroYaExisteException("978-0451524935"));

        mockMvc.perform(post("/api/libros").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void actualizarDevuelve200YElLibroActualizado() throws Exception {
        given(libroService.actualizar(eq("978-0451524935"), any(Libro.class)))
                .willReturn(new Libro("1984", "George Orwell", "978-0451524935", "Distopía", 20));

        mockMvc.perform(put("/api/libros/978-0451524935").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponibles").value(20));
    }

    @Test
    void actualizarLibroInexistenteDevuelve404() throws Exception {
        given(libroService.actualizar(eq("000"), any(Libro.class)))
                .willThrow(new LibroNoEncontradoException("000"));

        mockMvc.perform(put("/api/libros/000").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/libros/978-0451524935"))
                .andExpect(status().isNoContent());

        verify(libroService).eliminar("978-0451524935");
    }

    @Test
    void eliminarLibroInexistenteDevuelve404() throws Exception {
        doThrow(new LibroNoEncontradoException("000")).when(libroService).eliminar("000");

        mockMvc.perform(delete("/api/libros/000"))
                .andExpect(status().isNotFound());
    }
}
