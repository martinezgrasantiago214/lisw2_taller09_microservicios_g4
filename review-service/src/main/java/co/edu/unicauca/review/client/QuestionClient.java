package co.edu.unicauca.review.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class QuestionClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public QuestionClient(RestTemplate restTemplate, @Value("${services.question.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public QuestionInfo findById(Long questionId) {
        return RemoteCalls.call("Question Service",
                () -> restTemplate.getForObject(baseUrl + "/questions/{id}", QuestionInfo.class, questionId));
    }
}
