package ap1.luis.rivas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TranslateRequest {

    private String q;       // texto a traducir
    private String source;  // idioma origen, ej: "en" (auto = detección automática)
    private String target;  // idioma destino, ej: "es"
}
