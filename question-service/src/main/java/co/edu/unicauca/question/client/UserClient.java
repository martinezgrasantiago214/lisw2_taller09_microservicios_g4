package co.edu.unicauca.question.client;

import co.edu.unicauca.question.exception.ApiException;
import co.edu.unicauca.question.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Cliente REST síncrono hacia el User Service (:8081). */
@Component
public class UserClient {

    private static final String SERVICE = "User Service";

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserClient(RestTemplate restTemplate, @Value("${services.user.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /** Identifica al usuario a partir del encabezado Authorization: Bearer <token>. */
    public AuthenticatedUser authenticate(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new UnauthorizedException("Falta el encabezado Authorization: Bearer <token>");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        try {
            return RemoteCalls.call(SERVICE, () -> restTemplate.exchange(baseUrl + "/auth/validate", HttpMethod.GET,
                    new HttpEntity<>(headers), AuthenticatedUser.class).getBody());
        } catch (ApiException ex) {
            if (ex.getStatus() == HttpStatus.UNAUTHORIZED) {
                throw new UnauthorizedException("Token inválido o sesión cerrada");
            }
            throw ex;
        }
    }

    public AuthenticatedUser findById(Long userId) {
        return RemoteCalls.call(SERVICE,
                () -> restTemplate.getForObject(baseUrl + "/users/{id}", AuthenticatedUser.class, userId));
    }
}
