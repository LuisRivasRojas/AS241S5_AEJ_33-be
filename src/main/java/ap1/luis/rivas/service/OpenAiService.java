package ap1.luis.rivas.service;

import ap1.luis.rivas.model.AiResponse;
import ap1.luis.rivas.model.OpenAiRequest;
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
public class OpenAiService {

    private final WebClient.Builder webClientBuilder;
    private final AiResponseRepository aiResponseRepository;

    @Value("${ai.apis.openai.api-key}")
    private String apiKey;

    @Value("${ai.apis.openai.base-url}")
    private String baseUrl;

    @Value("${ai.apis.openai.host}")
    private String host;

    @Value("${ai.apis.openai.model}")
    private String model;

    @Value("${ai.apis.openai.max-tokens}")
    private Integer maxTokens;

    @Value("${ai.apis.openai.temperature}")
    private Double temperature;

    public Mono<AiResponse> chatWithOpenAI(String prompt) {
        long startTime = System.currentTimeMillis();

        // El endpoint de RapidAPI usa: {"messages": [...], "web_access": false}
        OpenAiRequest request = OpenAiRequest.createChatRequest(prompt, model, temperature, maxTokens);

        WebClient webClient = webClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader("x-rapidapi-key", apiKey)
            .defaultHeader("x-rapidapi-host", host)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

        return webClient.post()
            .uri("/conversationllama")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(raw -> {
                long responseTime = System.currentTimeMillis() - startTime;
                String content = null;
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(raw);
                    // Esta API devuelve {"result": "...", "status": true}
                    if (node.has("result")) content = node.get("result").asText();
                    else if (node.has("text")) content = node.get("text").asText();
                    else if (node.has("output")) content = node.get("output").asText();
                    else content = raw;
                } catch (Exception e) {
                    content = raw;
                }
                AiResponse aiResponse = new AiResponse(
                    "OPENAI_RAPIDAPI", model, prompt, content,
                    null, temperature, (int) responseTime, "SUCCESS"
                );
                log.info("OpenAI/RapidAPI response generated in {} ms", responseTime);
                return aiResponseRepository.save(aiResponse);
            })
            .onErrorResume(error -> {
                long responseTime = System.currentTimeMillis() - startTime;
                log.error("Error calling OpenAI/RapidAPI: {}", error.getMessage());

                AiResponse errorResponse = new AiResponse(
                    "OPENAI_RAPIDAPI", model, prompt, null, null,
                    temperature, (int) responseTime, "ERROR"
                );
                errorResponse.setErrorMessage(error.getMessage());
                return aiResponseRepository.save(errorResponse);
            });
    }
}
