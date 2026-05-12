package ap1.luis.rivas.repository.chat;

import ap1.luis.rivas.model.AiResponse;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatResponseRepository extends ReactiveMongoRepository<AiResponse, String> {

    Flux<AiResponse> findByStatus(String status);
}
