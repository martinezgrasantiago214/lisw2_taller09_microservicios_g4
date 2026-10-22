package co.edu.unicauca.review.client;

import co.edu.unicauca.review.exception.ApiException;
import co.edu.unicauca.review.exception.ServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.function.Supplier;

/**
 * Traduce los fallos de una llamada REST síncrona a excepciones de negocio:
 * - servicio caído o timeout  -> 503 Service Unavailable
 * - error 5xx del servicio    -> 503 Service Unavailable
 * - error 4xx del servicio    -> se propaga el mismo código y mensaje
 */
public final class RemoteCalls {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RemoteCalls() {
    }

    public static <T> T call(String serviceName, Supplier<T> call) {
        try {
            return call.get();
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException(serviceName + " no está disponible (" + ex.getMostSpecificCause().getMessage() + ")");
        } catch (HttpServerErrorException ex) {
            throw new ServiceUnavailableException(serviceName + " respondió con error " + ex.getStatusCode().value());
        } catch (HttpStatusCodeException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            throw new ApiException(status, remoteMessage(ex, serviceName));
        }
    }

    public static String remoteMessage(HttpStatusCodeException ex, String serviceName) {
        try {
            JsonNode body = MAPPER.readTree(ex.getResponseBodyAsString());
            if (body != null && body.hasNonNull("message")) {
                return serviceName + ": " + body.get("message").asText();
            }
        } catch (Exception ignored) {
            // cuerpo vacío o no JSON
        }
        return serviceName + " respondió " + ex.getStatusCode().value();
    }
}
