package com.aydsii.calculadora.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.model.ConversionDivisa;
import com.aydsii.calculadora.service.DivisaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

// @RestController: controlador REST; devuelve el body (JSON) de la respuesta.
@RestController
// @RequestMapping: prefijo común de las rutas de divisas.
@RequestMapping("/api/divisas")
// @Tag (Swagger/OpenAPI): agrupa este endpoint bajo la sección "Divisas".
@Tag(name = "Divisas", description = "Conversor de divisas que consulta un servicio externo")
public class DivisaController {

    private final DivisaService divisaService;

    public DivisaController(DivisaService divisaService) {
        this.divisaService = divisaService;
    }

    @GetMapping("/convertir")
    @Operation(summary = "Convierte un monto entre dos monedas",
            description = "Valida los parámetros, consulta el servicio externo y devuelve solo los datos relevantes. "
                    + "El Controller solo recibe los @RequestParam y devuelve la respuesta HTTP; la lógica está en el Service.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversión realizada"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos (monto <= 0 o código de moneda mal formado)"),
            @ApiResponse(responseCode = "502", description = "No se pudo obtener la información del servicio externo")
    })
    public ConversionDivisa convertir(
            @Parameter(description = "Cantidad de dinero a convertir, debe ser mayor que 0", example = "100")
            @RequestParam @Positive(message = "El monto debe ser mayor que 0") double monto,
            @Parameter(description = "Código ISO de la moneda de origen (3 letras)", example = "USD")
            @RequestParam @Pattern(regexp = "[A-Za-z]{3}",
                    message = "El código de origen debe ser de 3 letras") String origen,
            @Parameter(description = "Código ISO de la moneda de destino (3 letras)", example = "ARS")
            @RequestParam @Pattern(regexp = "[A-Za-z]{3}",
                    message = "El código de destino debe ser de 3 letras") String destino) {
        return divisaService.convertir(monto, origen, destino);
    }
}
