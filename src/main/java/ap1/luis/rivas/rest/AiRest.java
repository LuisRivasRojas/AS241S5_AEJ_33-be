package ap1.luis.rivas.rest;

import ap1.luis.rivas.dto.ChatRequest;
import ap1.luis.rivas.model.AiResponse;
import ap1.luis.rivas.repository.AiResponseRepository;
import ap1.luis.rivas.service.OpenAiService;
import ap1.luis.rivas.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiRest {

    private final OpenAiService openAiService;
    private final TranslateService translateService;
    private final AiResponseRepository aiResponseRepository;

    // POST /api/ia/chat  - Chat con Llama 3.3 70b via RapidAPI
    @PostMapping("/chat")
    public Mono<ResponseEntity<AiResponse>> chat(@RequestBody ChatRequest request) {
        return openAiService.chatWithOpenAI(request.getPrompt())
            .map(ResponseEntity::ok);
    }

    // POST /api/ia/translate?source=en&target=es
    @PostMapping("/translate")
    public Mono<ResponseEntity<AiResponse>> translate(
            @RequestBody ChatRequest request,
            @RequestParam(defaultValue = "auto") String source,
            @RequestParam String target) {
        return translateService.translate(request.getPrompt(), source, target)
            .map(ResponseEntity::ok);
    }

    // GET /api/ia/history - ver todas las respuestas guardadas
    @GetMapping("/history")
    public Flux<AiResponse> history() {
        return aiResponseRepository.findAll();
    }

    // GET /api/ia/history/{provider} - filtrar por proveedor (OPENAI_RAPIDAPI, DEEP_TRANSLATE)
    @GetMapping("/history/{provider}")
    public Flux<AiResponse> historyByProvider(@PathVariable String provider) {
        return aiResponseRepository.findByApiProvider(provider.toUpperCase());
    }
}
