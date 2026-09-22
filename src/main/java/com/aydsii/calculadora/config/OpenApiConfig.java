package com.aydsii.calculadora.config;

import org.springdoc.core.models.GroupedOpenApi;
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
                        .description("API REST de demo (patrón MVC): operaciones aritméticas básicas, "
                                + "un CRUD de libros y un conversor de divisas con almacenamiento en memoria, "
                                + "alta de clientes con validación de datos (Bean Validation) y un historial "
                                + "de pedidos con filtros combinables, todo persistido en MySQL")
                        .version("1.0.0"));
    }

    // Grupo de Swagger UI dedicado al TP: solo los endpoints de Ventas, Catálogo y Divisas
    // (Ejercicios 1 a 3). Definir un GroupedOpenApi hace aparecer un selector de grupos en
    // Swagger UI; se agrega también "Todos los endpoints" para no perder acceso al resto.
    @Bean
    public GroupedOpenApi tpSpringGroup() {
        return GroupedOpenApi.builder()
                .group("tp-spring")
                .displayName("TP Spring")
                .pathsToMatch("/api/ventas/**", "/api/catalogo/**", "/api/divisas/**")
                .build();
    }

    @Bean
    public GroupedOpenApi todosLosEndpointsGroup() {
        return GroupedOpenApi.builder()
                .group("todos")
                .displayName("Todos los endpoints")
                .pathsToMatch("/api/**")
                .build();
    }
}
