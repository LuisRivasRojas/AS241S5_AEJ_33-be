package ap1.luis.rivas.service;

import ap1.luis.rivas.model.AiResponse;
import ap1.luis.rivas.model.TranslateRequest;
import ap1.luis.rivas.model.TranslateResponse;
import ap1.luis.rivas.repository.AiResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslateService {

    private final WebClient.Builder webClientBuilder;
    private final AiResponseRepository aiResponseRepository;

    @Value("${ai.apis.translate.api-key}")
    private String apiKey;

    @Value("${ai.apis.translate.base-url}")
    private String baseUrl;

    @Value("${ai.apis.translate.host}")
    private String host;

    public Mono<AiResponse> translate(String text, String source, String target) {
        long startTime = System.currentTimeMillis();

        TranslateRequest request = new TranslateRequest(text, source, target);

        WebClient webClient = webClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader("x-rapidapi-key", apiKey)
            .defaultHeader("x-rapidapi-host", host)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

        return webClient.post()
            .uri("/language/translate/v2")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(TranslateResponse.class)
            .map(response -> {
                long responseTime = System.currentTimeMillis() - startTime;
                String translated = response.getTranslatedText();
                return new AiResponse(
                    "DEEP_TRANSLATE", "deep-translate",
                    text + " [" + source + "->" + target + "]",
                    translated, null, null, (int) responseTime, "SUCCESS"
                );
            })
            .doOnSuccess(aiResponse -> {
                log.info("Translation done in {} ms", aiResponse.getResponseTimeMs());
                aiResponseRepository.save(aiResponse).subscribe();
            })
            .onErrorResume(error -> {
                long responseTime = System.currentTimeMillis() - startTime;
                log.error("Error calling Deep Translate: {}", error.getMessage());
                AiResponse errorResponse = new AiResponse(
                    "DEEP_TRANSLATE", "deep-translate",
                    text, null, null, null, (int) responseTime, "ERROR"
                );
                errorResponse.setErrorMessage(error.getMessage());
                return aiResponseRepository.save(errorResponse);
            });
    }
}
