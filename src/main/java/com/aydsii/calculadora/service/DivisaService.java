package com.aydsii.calculadora.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.aydsii.calculadora.exception.ServicioExternoException;
import com.aydsii.calculadora.model.ConversionDivisa;

// @Service: capa de negocio; acá vive la llamada a Frankfurter y el armado de la respuesta propia.
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

        // Se pide la tasa para 1 unidad (amount=1) y se aplica el monto acá, en vez de pedirle a
        // Frankfurter el monto ya convertido: así tasaCambio y montoConvertido quedan consistentes
        // entre sí sin tener que despejar la tasa a partir del monto convertido.
        FrankfurterResponse respuesta = consultarServicioExterno(monedaOrigen, monedaDestino);
        Double tasaCambio = respuesta.rates().get(monedaDestino);
        if (tasaCambio == null) {
            throw new ServicioExternoException(
                    "El servicio externo no devolvió una tasa para " + monedaOrigen + " -> " + monedaDestino);
        }

        double montoConvertido = monto * tasaCambio;

        return new ConversionDivisa(monto, monedaOrigen, monedaDestino, redondear(tasaCambio),
                redondear(montoConvertido), respuesta.date());
    }

    /**
     * Realiza la petición HTTP a Frankfurter y captura los posibles problemas de comunicación
     * (sin respuesta, timeout, error HTTP, moneda inexistente), traduciéndolos a una
     * ServicioExternoException (-> HTTP 502 Bad Gateway).
     */
    private FrankfurterResponse consultarServicioExterno(String origen, String destino) {
        try {
            FrankfurterResponse respuesta = restClient.get()
                    .uri("/latest?amount=1&from={origen}&to={destino}", origen, destino)
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (respuesta == null || respuesta.rates() == null || respuesta.rates().isEmpty()) {
                throw new ServicioExternoException("El servicio externo no devolvió información de tasas");
            }
            return respuesta;
        } catch (RestClientResponseException ex) {
            // La API externa respondió con un código de error HTTP (4xx/5xx), por ejemplo cuando
            // se pide una moneda que no existe para Frankfurter.
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

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    /** Forma de la respuesta de Frankfurter: {"amount":1.0,"base":"USD","date":"2026-09-22","rates":{"ARS":1234.56}} */
    private record FrankfurterResponse(double amount, String base, LocalDate date, Map<String, Double> rates) {
    }
}
