package com.aydsii.calculadora.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.model.Libro;
import com.aydsii.calculadora.service.LibroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// @RestController: marca la clase como controlador REST; cada método devuelve el body de la respuesta (JSON), no una vista.
@RestController
// @RequestMapping: prefijo de ruta común a todos los endpoints de esta clase.
@RequestMapping("/api/libros")
// @Tag (Swagger/OpenAPI): agrupa estos endpoints bajo la sección "Libros" en Swagger UI.
@Tag(name = "Libros", description = "CRUD del catálogo de libros (almacenados en memoria)")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los libros", description = "Devuelve el catálogo completo")
    public List<Libro> listar() {
        return libroService.listarTodos();
    }

    @GetMapping("/{isbn}")
    @Operation(summary = "Ver un libro en particular", description = "Busca un libro por su ISBN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese ISBN")
    })
    public Libro obtener(@Parameter(description = "ISBN del libro") @PathVariable String isbn) {
        return libroService.obtener(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un libro", description = "Agrega un libro nuevo al catálogo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Libro creado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un libro con ese ISBN")
    })
    public Libro crear(@RequestBody Libro libro) {
        return libroService.crear(libro);
    }

    @PutMapping("/{isbn}")
    @Operation(summary = "Editar un libro", description = "Actualiza los datos de un libro existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro actualizado"),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese ISBN")
    })
    public Libro actualizar(@Parameter(description = "ISBN del libro") @PathVariable String isbn,
            @RequestBody Libro libro) {
        return libroService.actualizar(isbn, libro);
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Borrar un libro", description = "Elimina un libro del catálogo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Libro eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un libro con ese ISBN")
    })
    public void eliminar(@Parameter(description = "ISBN del libro") @PathVariable String isbn) {
        libroService.eliminar(isbn);
    }
}
