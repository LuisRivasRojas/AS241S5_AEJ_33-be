package ap1.luis.rivas.repository;

import ap1.luis.rivas.model.AiResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AiResponseRepository extends ReactiveMongoRepository<AiResponse, String> {
    
    Flux<AiResponse> findByApiProvider(String apiProvider);
    
    Flux<AiResponse> findByApiProviderOrderByTimestampDesc(String apiProvider);
    
    Flux<AiResponse> findByStatus(String status);
    
    Mono<AiResponse> findFirstByApiProviderOrderByTimestampDesc(String apiProvider);
}
