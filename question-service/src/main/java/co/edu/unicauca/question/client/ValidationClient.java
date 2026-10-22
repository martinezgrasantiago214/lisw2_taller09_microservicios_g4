package co.edu.unicauca.question.client;

import co.edu.unicauca.question.dto.CreateQuestionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** Cliente REST síncrono hacia el Validation Service (:8082). */
@Component
public class ValidationClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ValidationClient(RestTemplate restTemplate, @Value("${services.validation.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ValidationResult validate(CreateQuestionRequest question) {
        return RemoteCalls.call("Validation Service",
                () -> restTemplate.postForObject(baseUrl + "/validate", question, ValidationResult.class));
    }
}
