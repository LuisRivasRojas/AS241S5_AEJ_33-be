package ap1.luis.rivas.repository.translate;

import ap1.luis.rivas.model.AiResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TranslateResponseRepository extends ReactiveMongoRepository<AiResponse, String> {

    Flux<AiResponse> findByStatus(String status);
}
