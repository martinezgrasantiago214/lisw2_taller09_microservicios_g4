package co.edu.unicauca.review.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/** Cliente REST síncrono hacia el Lifecycle Service (:8084), autoridad sobre el estado. */
@Component
public class LifecycleClient {

    private static final String SERVICE = "Lifecycle Service";

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public LifecycleClient(RestTemplate restTemplate, @Value("${services.lifecycle.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public LifecycleState getState(Long questionId) {
        return RemoteCalls.call(SERVICE,
                () -> restTemplate.getForObject(baseUrl + "/lifecycle/{id}", LifecycleState.class, questionId));
    }

    public LifecycleState requestTransition(Long questionId, String targetState, String changedBy, String comment) {
        // HashMap y no Map.of: Map.of lanza NullPointerException si comment es null
        // (por ejemplo, al aprobar una pregunta sin observaciones).
        Map<String, String> body = new HashMap<>();
        body.put("targetState", targetState);
        body.put("changedBy", changedBy);
        body.put("comment", comment == null ? "" : comment);
        return RemoteCalls.call(SERVICE, () -> restTemplate.postForObject(
                baseUrl + "/lifecycle/{id}/transition", body, LifecycleState.class, questionId));
    }
}
