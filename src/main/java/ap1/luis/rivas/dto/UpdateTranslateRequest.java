package ap1.luis.rivas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTranslateRequest {

    @NotBlank
    private String prompt;

    @NotBlank
    private String source;

    @NotBlank
    private String target;
}
