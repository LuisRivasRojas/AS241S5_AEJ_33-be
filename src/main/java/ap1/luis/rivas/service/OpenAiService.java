package ap1.luis.rivas.service;

import ap1.luis.rivas.model.AiResponse;
import ap1.luis.rivas.model.OpenAiRequest;
import ap1.luis.rivas.repository.chat.ChatResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final WebClient.Builder webClientBuilder;
    private final ChatResponseRepository chatResponseRepository;

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
                String content = parseContent(raw);
                AiResponse aiResponse = new AiResponse(
                    "OPENAI_RAPIDAPI", model, prompt, content,
                    null, temperature, (int) responseTime, "SUCCESS"
                );
                log.info("OpenAI/RapidAPI response generated in {} ms", responseTime);
                return chatResponseRepository.save(aiResponse);
            })
            .onErrorResume(error -> {
                long responseTime = System.currentTimeMillis() - startTime;
                log.error("Error calling OpenAI/RapidAPI: {}", error.getMessage());
                AiResponse errorResponse = new AiResponse(
                    "OPENAI_RAPIDAPI", model, prompt, null, null,
                    temperature, (int) responseTime, "ERROR"
                );
                errorResponse.setErrorMessage(error.getMessage());
                return chatResponseRepository.save(errorResponse);
            });
    }

    public Mono<AiResponse> rechat(String id, String newPrompt) {
        long startTime = System.currentTimeMillis();

        OpenAiRequest request = OpenAiRequest.createChatRequest(newPrompt, model, temperature, maxTokens);

        WebClient webClient = webClientBuilder
            .baseUrl(baseUrl)
            .defaultHeader("x-rapidapi-key", apiKey)
            .defaultHeader("x-rapidapi-host", host)
            .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .build();

        return chatResponseRepository.findById(id)
            .flatMap(existing ->
                webClient.post()
                    .uri("/conversationllama")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(raw -> {
                        long responseTime = System.currentTimeMillis() - startTime;
                        existing.setPrompt(newPrompt);
                        existing.setResponse(parseContent(raw));
                        existing.setResponseTimeMs((int) responseTime);
                        existing.setStatus("SUCCESS");
                        existing.setErrorMessage(null);
                        log.info("Rechat done in {} ms", responseTime);
                        return chatResponseRepository.save(existing);
                    })
                    .onErrorResume(error -> {
                        long responseTime = System.currentTimeMillis() - startTime;
                        log.error("Error rechatting: {}", error.getMessage());
                        existing.setPrompt(newPrompt);
                        existing.setStatus("ERROR");
                        existing.setErrorMessage(error.getMessage());
                        existing.setResponseTimeMs((int) responseTime);
                        return chatResponseRepository.save(existing);
                    })
            )
            .switchIfEmpty(Mono.error(new RuntimeException("Registro no encontrado: " + id)));
    }

    private String parseContent(String raw) {
        try {
            com.fasterxml.jackson.databind.JsonNode node = MAPPER.readTree(raw);
            if (node.has("result")) return node.get("result").asText();
            if (node.has("text"))   return node.get("text").asText();
            if (node.has("output")) return node.get("output").asText();
        } catch (Exception ignored) {}
        return raw;
    }
}
