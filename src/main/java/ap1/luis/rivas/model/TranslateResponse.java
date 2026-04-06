package ap1.luis.rivas.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateResponse {

    // Deep Translate devuelve: {"data": {"translations": {"translatedText": ["..."]}}}
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Translations translations;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Translations {
        // La API devuelve un array, no un String
        private List<String> translatedText;
    }

    public String getTranslatedText() {
        if (data != null && data.getTranslations() != null) {
            List<String> list = data.getTranslations().getTranslatedText();
            return (list != null && !list.isEmpty()) ? list.get(0) : null;
        }
        return null;
    }
}
