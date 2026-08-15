package com.aydsii.calculadora.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aydsii.calculadora.model.ResultadoOperacion;
import com.aydsii.calculadora.service.CalculadoraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/calculadora")
@Tag(name = "Calculadora", description = "Operaciones aritméticas básicas")
public class CalculadoraController {

    private final CalculadoraService calculadoraService;

    public CalculadoraController(CalculadoraService calculadoraService) {
        this.calculadoraService = calculadoraService;
    }

    @GetMapping("/sumar")
    @Operation(summary = "Suma dos números", description = "Devuelve el resultado de a + b")
    public ResultadoOperacion sumar(@Parameter(description = "Primer número") @RequestParam double a,
            @Parameter(description = "Segundo número") @RequestParam double b) {
        return new ResultadoOperacion(a, b, "suma", calculadoraService.sumar(a, b));
    }

    @GetMapping("/restar")
    @Operation(summary = "Resta dos números", description = "Devuelve el resultado de a - b")
    public ResultadoOperacion restar(@Parameter(description = "Primer número") @RequestParam double a,
            @Parameter(description = "Segundo número") @RequestParam double b) {
        return new ResultadoOperacion(a, b, "resta", calculadoraService.restar(a, b));
    }

    @GetMapping("/multiplicar")
    @Operation(summary = "Multiplica dos números", description = "Devuelve el resultado de a * b")
    public ResultadoOperacion multiplicar(@Parameter(description = "Primer número") @RequestParam double a,
            @Parameter(description = "Segundo número") @RequestParam double b) {
        return new ResultadoOperacion(a, b, "multiplicacion", calculadoraService.multiplicar(a, b));
    }

    @GetMapping("/dividir")
    @Operation(summary = "Divide dos números", description = "Devuelve el resultado de a / b. Falla si b es 0")
    public ResultadoOperacion dividir(@Parameter(description = "Primer número") @RequestParam double a,
            @Parameter(description = "Segundo número") @RequestParam double b) {
        return new ResultadoOperacion(a, b, "division", calculadoraService.dividir(a, b));
    }
}
