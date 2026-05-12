package ap1.luis.rivas.rest;

import ap1.luis.rivas.dto.ChatRequest;
import ap1.luis.rivas.dto.UpdateTranslateRequest;
import ap1.luis.rivas.model.AiResponse;
import ap1.luis.rivas.repository.chat.ChatResponseRepository;
import ap1.luis.rivas.repository.translate.TranslateResponseRepository;
import ap1.luis.rivas.service.OpenAiService;
import ap1.luis.rivas.service.TranslateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class AiRest {

    private final OpenAiService openAiService;
    private final TranslateService translateService;
    private final ChatResponseRepository chatResponseRepository;
    private final TranslateResponseRepository translateResponseRepository;

    // POST /api/ia/chat
    @PostMapping("/chat")
    public Mono<ResponseEntity<AiResponse>> chat(@Valid @RequestBody ChatRequest request) {
        return openAiService.chatWithOpenAI(request.getPrompt())
            .map(ResponseEntity::ok);
    }

    // POST /api/ia/translate?source=en&target=es
    @PostMapping("/translate")
    public Mono<ResponseEntity<AiResponse>> translate(
            @Valid @RequestBody ChatRequest request,
            @RequestParam(defaultValue = "auto") String source,
            @RequestParam String target) {
        return translateService.translate(request.getPrompt(), source, target)
            .map(ResponseEntity::ok);
    }

    // GET /api/ia/history - todos los registros (chat + translate combinados)
    @GetMapping("/history")
    public Flux<AiResponse> history() {
        return Flux.merge(
            chatResponseRepository.findAll(),
            translateResponseRepository.findAll()
        );
    }

    // GET /api/ia/history/OPENAI_RAPIDAPI - solo chat
    // GET /api/ia/history/DEEP_TRANSLATE   - solo translate
    @GetMapping("/history/{provider}")
    public Flux<AiResponse> historyByProvider(@PathVariable String provider) {
        return switch (provider.toUpperCase()) {
            case "OPENAI_RAPIDAPI" -> chatResponseRepository.findAll();
            case "DEEP_TRANSLATE"  -> translateResponseRepository.findAll();
            default -> Flux.empty();
        };
    }

    // PUT /api/ia/history/{id}?provider=OPENAI_RAPIDAPI  — actualiza solo el prompt del chat
    @PutMapping("/history/{id}")
    public Mono<ResponseEntity<AiResponse>> update(
            @PathVariable String id,
            @RequestParam String provider,
            @RequestBody ChatRequest request) {

        String newPrompt = request.getPrompt();
        log.info("UPDATE id={} provider={} newPrompt={}", id, provider, newPrompt);

        if ("DEEP_TRANSLATE".equalsIgnoreCase(provider)) {
            // No debería llegar aquí, pero por seguridad devolvemos bad request
            return Mono.just(ResponseEntity.<AiResponse>badRequest().build());
        }
        return chatResponseRepository.findById(id)
            .doOnNext(r -> log.info("Found chat record: {}", r.getId()))
            .flatMap(existing -> {
                existing.setPrompt(newPrompt);
                return chatResponseRepository.save(existing);
            })
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.<AiResponse>notFound().build());
    }

    // PUT /api/ia/history/translate/{id} — re-traduce con el nuevo texto
    @PutMapping("/history/translate/{id}")
    public Mono<ResponseEntity<AiResponse>> updateTranslate(
            @PathVariable String id,
            @Valid @RequestBody UpdateTranslateRequest request) {
        log.info("RETRANSLATE id={} source={} target={}", id, request.getSource(), request.getTarget());
        return translateService.retranslate(id, request.getPrompt(), request.getSource(), request.getTarget())
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.<AiResponse>notFound().build());
    }

    // PUT /api/ia/history/chat/{id} — re-consulta la IA con el nuevo prompt
    @PutMapping("/history/chat/{id}")
    public Mono<ResponseEntity<AiResponse>> updateChat(
            @PathVariable String id,
            @RequestBody ChatRequest request) {
        log.info("RECHAT id={} newPrompt={}", id, request.getPrompt());
        return openAiService.rechat(id, request.getPrompt())
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.<AiResponse>notFound().build());
    }

    // DELETE /api/ia/history/{id}?provider=OPENAI_RAPIDAPI
    @DeleteMapping("/history/{id}")
    public Mono<ResponseEntity<Void>> delete(
            @PathVariable String id,
            @RequestParam String provider) {
        if ("DEEP_TRANSLATE".equalsIgnoreCase(provider)) {
            return translateResponseRepository.findById(id)
                .flatMap(existing -> translateResponseRepository.delete(existing)
                    .<ResponseEntity<Void>>thenReturn(ResponseEntity.noContent().build()))
                .defaultIfEmpty(ResponseEntity.<Void>notFound().build());
        }
        return chatResponseRepository.findById(id)
            .flatMap(existing -> chatResponseRepository.delete(existing)
                .<ResponseEntity<Void>>thenReturn(ResponseEntity.noContent().build()))
            .defaultIfEmpty(ResponseEntity.<Void>notFound().build());
    }
}
