package com.aydsii.calculadora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

// @Configuration: clase de configuración de Spring; define beans mediante métodos @Bean.
@Configuration
public class OpenApiConfig {

    // @Bean: Spring registra el objeto devuelto en el contenedor; springdoc lo usa para armar la doc de Swagger.
    @Bean
    public OpenAPI calculadoraOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Calculadora API")
                        .description("API REST de demo (patrón MVC): operaciones aritméticas básicas "
                                + "y un CRUD de libros con almacenamiento en memoria")
                        .version("1.0.0"));
    }
}
