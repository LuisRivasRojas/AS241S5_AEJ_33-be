package ap1.luis.rivas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
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
}
