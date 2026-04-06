package ap1.luis.rivas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_responses")
public class AiResponse {
    
    @Id
    private String id;
    
    private String apiProvider;
    
    private String model;
    
    private String prompt;
    
    private String response;
    
    private Integer tokensUsed;
    
    private Double temperature;
    
    private LocalDateTime timestamp;
    
    private Integer responseTimeMs;
    
    private String status;
    
    private String errorMessage;
    
    public AiResponse(String apiProvider, String model, String prompt, String response, 
                     Integer tokensUsed, Double temperature, Integer responseTimeMs, String status) {
        this.apiProvider = apiProvider;
        this.model = model;
        this.prompt = prompt;
        this.response = response;
        this.tokensUsed = tokensUsed;
        this.temperature = temperature;
        this.responseTimeMs = responseTimeMs;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }
}
