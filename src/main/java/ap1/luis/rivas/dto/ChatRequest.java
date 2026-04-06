package ap1.luis.rivas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    @NotBlank(message = "El prompt no puede estar vacío")
    private String prompt;
    
    private Integer maxTokens;
    
    private Double temperature;
}
