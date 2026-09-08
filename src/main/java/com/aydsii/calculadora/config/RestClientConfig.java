package com.aydsii.calculadora.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// @Configuration: clase de configuración de Spring; define beans mediante métodos @Bean.
@Configuration
public class RestClientConfig {

    // Servicio externo que se consulta para (simular) las tasas de cambio.
    // El ejercicio usa https://api.frankfurter.app; aquí se reemplaza por jsonplaceholder.
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    // @Bean: registra un RestClient ya configurado (baseUrl + timeouts) para inyectarlo en el service.
    @Bean
    public RestClient divisasRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(3));

        return RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(factory)
                .build();
    }
}
