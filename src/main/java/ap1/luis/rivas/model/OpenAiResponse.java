package ap1.luis.rivas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiResponse {
    
    private String id;
    private String object;
    private String model;
    private Long created;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Integer index;
        private Message message;
        private String finish_reason;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
    
    public String getFirstResponse() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }
}
