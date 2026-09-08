package com.aydsii.calculadora.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.ResourceAccessException;

import com.aydsii.calculadora.exception.ServicioExternoException;
import com.aydsii.calculadora.model.ConversionDivisa;

// @Service: capa de negocio; acá vive la llamada al servicio externo y el armado de la respuesta.
@Service
public class DivisaService {

    private final RestClient restClient;

    // Se inyecta el RestClient configurado como bean en RestClientConfig.
    public DivisaService(RestClient divisasRestClient) {
        this.restClient = divisasRestClient;
    }

    public ConversionDivisa convertir(double monto, String origen, String destino) {
        String monedaOrigen = origen.toUpperCase();
        String monedaDestino = destino.toUpperCase();

        List<Post> respuestaExterna = consultarServicioExterno();

        double tasaCambio = calcularTasaSimulada(monedaOrigen, monedaDestino, respuestaExterna.size());
        double montoConvertido = monto * tasaCambio;

        return new ConversionDivisa(monto, monedaOrigen, monedaDestino,
                redondear(tasaCambio), redondear(montoConvertido), LocalDate.now());
    }

    /**
     * Realiza la petición HTTP al servicio externo y captura los posibles problemas
     * de comunicación, traduciéndolos a una ServicioExternoException (-> HTTP 502).
     */
    private List<Post> consultarServicioExterno() {
        try {
            List<Post> posts = restClient.get()
                    .uri("/posts")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Post>>() {
                    });

            if (posts == null || posts.isEmpty()) {
                throw new ServicioExternoException("El servicio externo no devolvió información");
            }
            return posts;
        } catch (RestClientResponseException ex) {
            // La API externa respondió con un código de error HTTP (4xx / 5xx).
            throw new ServicioExternoException(
                    "El servicio externo respondió con un error HTTP " + ex.getStatusCode().value(), ex);
        } catch (ResourceAccessException ex) {
            // No hubo respuesta: el servicio no está disponible o se produjo un timeout.
            throw new ServicioExternoException(
                    "No se pudo contactar al servicio externo (sin respuesta o timeout)", ex);
        } catch (RestClientException ex) {
            // Cualquier otro problema al comunicarse o al parsear la respuesta.
            throw new ServicioExternoException("Error al comunicarse con el servicio externo", ex);
        }
    }

    /**
     * jsonplaceholder no expone tasas de cambio, así que se deriva una tasa
     * determinística a partir del par de monedas y de la cantidad de registros
     * recibidos. Con Frankfurter este valor vendría directamente en la respuesta.
     */
    private double calcularTasaSimulada(String origen, String destino, int cantidadRegistros) {
        int semilla = Math.abs((origen + destino).hashCode() % 1000);
        return cantidadRegistros + semilla / 100.0;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Estructura mínima de un registro devuelto por el servicio externo. */
    private record Post(Long userId, Long id, String title, String body) {
    }
}
