package com.aydsii.calculadora.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// @Configuration: clase de configuración de Spring; define beans mediante métodos @Bean.
@Configuration
public class RestClientConfig {

    // API pública y gratuita de tasas de cambio (TP Spring, Ejercicio 3). El dominio histórico
    // api.frankfurter.app redirige (301) a api.frankfurter.dev/v1; se apunta directo al nuevo
    // dominio para no depender de ese redirect.
    private static final String BASE_URL = "https://api.frankfurter.dev/v1";

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
