# AS241S5_AEJ_33-be
# IA API

Proyecto Spring Boot WebFlux que integra dos APIs de inteligencia artificial via RapidAPI: chat con Llama 3.3 70b y traducción de texto con Deep Translate. Las respuestas se persisten automáticamente en MongoDB.

---

## Tecnologías

- Java 17
- Spring Boot 3.4.1
- Spring WebFlux (programación reactiva)
- MongoDB Reactivo
- Lombok
- RapidAPI (Llama 3.3 70b + Deep Translate)

---

## Estructura del proyecto

```
src/main/java/ap1/luis/rivas/
├── config/
│   └── WebClientConfig.java        # Bean de WebClient
├── dto/
│   ├── ChatRequest.java             # Request de entrada
│   └── ChatResponse.java            # Response de salida
├── model/
│   ├── AiResponse.java              # Entidad MongoDB
│   ├── OpenAiRequest.java           # Request para Llama API
│   ├── OpenAiResponse.java          # Response de Llama API
│   ├── TranslateRequest.java        # Request para Deep Translate
│   └── TranslateResponse.java       # Response de Deep Translate
├── repository/
│   └── AiResponseRepository.java    # Repositorio reactivo MongoDB
├── rest/
│   └── AiRest.java                  # Controlador REST
├── service/
│   ├── OpenAiService.java           # Servicio Llama 3.3 70b
│   └── TranslateService.java        # Servicio Deep Translate
└── Application.java
```

---

## Configuración

Las variables de entorno necesarias:

| Variable | Descripción |
|---|---|
| `MONGODB_URI` | URI de conexión a MongoDB Atlas |
| `OPENAI_API_KEY` | API Key de RapidAPI para Llama |
| `TRANSLATE_API_KEY` | API Key de RapidAPI para Deep Translate |

Si no se definen, el `application.yaml` usa los valores por defecto configurados.

---

## Endpoints

### POST /api/ia/chat
Chat con el modelo Llama 3.3 70b via RapidAPI.

**Body:**
```json
{
    "prompt": "¿Qué es una API REST?"
}
```

**Response:**
```json
{
    "id": "64f3a...",
    "apiProvider": "OPENAI_RAPIDAPI",
    "model": "llama3.3-70b",
    "prompt": "¿Qué es una API REST?",
    "response": "Una API REST es...",
    "status": "SUCCESS",
    "timestamp": "2026-04-05T22:00:00"
}
```

---

### POST /api/ia/translate
Traducción de texto con Deep Translate.

**Params:**
- `source` - idioma origen (default: `auto`)
- `target` - idioma destino (requerido)

**Códigos de idioma:** `es`, `en`, `fr`, `pt`, `de`, `it`, `ja`, `zh`, etc.

**Body:**
```json
{
    "prompt": "Hello World"
}
```

**Ejemplo:**
```
POST /api/ia/translate?source=en&target=es
```

---

### GET /api/ia/history
Devuelve todas las respuestas guardadas en MongoDB.

### GET /api/ia/history/{provider}
Filtra por proveedor.