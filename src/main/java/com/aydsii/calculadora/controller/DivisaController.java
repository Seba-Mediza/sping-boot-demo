package com.aydsii.calculadora.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.dto.ApiResponse;
import com.aydsii.calculadora.model.ConversionDivisa;
import com.aydsii.calculadora.service.DivisaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

// @RestController: controlador REST; devuelve el body (JSON) de la respuesta.
@RestController
// @RequestMapping: prefijo común de las rutas de divisas.
@RequestMapping("/api/divisas")
// @Tag (Swagger/OpenAPI): agrupa este endpoint bajo la sección de divisas del TP.
@Tag(name = "TP Spring - Divisas", description = "Conversor de divisas que consulta la API pública Frankfurter")
public class DivisaController {

    private final DivisaService divisaService;

    public DivisaController(DivisaService divisaService) {
        this.divisaService = divisaService;
    }

    @GetMapping("/convertir")
    @Operation(summary = "Convierte un monto entre dos monedas",
            description = "Valida los parámetros, consulta la API externa Frankfurter y devuelve solo los datos "
                    + "relevantes. El Controller solo recibe los @RequestParam y arma la respuesta HTTP; la "
                    + "llamada externa y el procesamiento viven en el Service.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Conversión realizada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Parámetros inválidos (monto <= 0 o código de moneda mal formado)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "No se pudo obtener la información del servicio externo (no responde, timeout, error HTTP o moneda inexistente)")
    })
    public ResponseEntity<ApiResponse<ConversionDivisa>> convertir(
            @Parameter(description = "Cantidad de dinero a convertir, debe ser mayor que 0", example = "100")
            @RequestParam @Positive(message = "el monto debe ser mayor que 0") double monto,
            @Parameter(description = "Código ISO de la moneda de origen (3 letras)", example = "USD")
            @RequestParam @Pattern(regexp = "[A-Za-z]{3}",
                    message = "el código de origen debe ser un código de moneda de 3 letras") String origen,
            @Parameter(description = "Código ISO de la moneda de destino (3 letras)", example = "EUR")
            @RequestParam @Pattern(regexp = "[A-Za-z]{3}",
                    message = "el código de destino debe ser un código de moneda de 3 letras") String destino) {
        ConversionDivisa resultado = divisaService.convertir(monto, origen, destino);
        return ResponseEntity
                .ok(ApiResponse.of(HttpStatus.OK.value(), "Conversión realizada correctamente", resultado));
    }
}
