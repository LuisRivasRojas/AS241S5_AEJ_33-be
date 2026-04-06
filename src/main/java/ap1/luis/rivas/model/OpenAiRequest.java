package ap1.luis.rivas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiRequest {

    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer max_tokens;

    @JsonProperty("web_access")
    private Boolean webAccess = false;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    public static OpenAiRequest createChatRequest(String prompt, String model, Double temperature, Integer maxTokens) {
        Message message = new Message("user", prompt);
        OpenAiRequest req = new OpenAiRequest();
        req.setModel(model);
        req.setMessages(List.of(message));
        req.setTemperature(temperature);
        req.setMax_tokens(maxTokens);
        req.setWebAccess(false);
        return req;
    }
}
